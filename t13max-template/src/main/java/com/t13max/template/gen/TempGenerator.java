package com.t13max.template.gen;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.t13max.template.enums.DataTypeEnum;
import com.t13max.template.exception.TemplateException;
import com.t13max.template.listener.TemplateReadListener;
import com.t13max.template.util.Log;
import com.t13max.util.FileUtil;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

import static java.lang.System.exit;

/**
 * 模板类/json 生成
 *
 * @author: t13max
 * @since: 15:58 2024/8/5
 */
public class TempGenerator {

    private GenerateContext generateContext;

    public static void main(String[] args) throws Exception {

        TempGenerator tempGenerator = new TempGenerator();

        tempGenerator.run();

    }

    public void run() throws Exception {

        //打印路径
        printEnv();

        //读取
        read();

        //先清除
        cleanUp();

        //生成json
        genJson();

        //生成java
        genJava();

        //退出
        exit();
    }

    private void exit() {
        Log.template.info("生成完毕!!!");
        System.exit(0);
    }

    /**
     * 生成Java类
     *
     * @Author t13max
     * @Date 15:37 2024/8/6
     */
    private void genJava() throws Exception {
        for (ExcelData excelData : generateContext.getExcelDataMap().values()) {
            for (SheetData sheetData : excelData.getSheetDataMap().values()) {

                Map<String, Object> resMap = new HashMap<>(16);
                String entityName = sheetData.getSheetName();
                // 包名
                resMap.put("package", GenerateConfig.packageName);
                // Excel文件名
                resMap.put("excelName", excelData.getExcelName());
                // 类名
                resMap.put("entityName", entityName);
                // 类注释
                resMap.put("entityNote", sheetData.getSheetNote());

                // 字段名
                Set<Map<String, String>> properties = new LinkedHashSet<>();
                resMap.put("props", properties);

                // 构造器参数
                StringBuilder paramMethod = new StringBuilder();

                for (int i = 0; i < sheetData.getNameList().size(); i++) {
                    String fieldName = sheetData.getNameList().get(i);
                    String fieldType = sheetData.getTypeList().get(i);
                    Map<String, String> prop = new HashMap<>();

                    DataTypeEnum dataTypeEnum = DataTypeEnum.of(fieldType);
                    if (dataTypeEnum == null) throw new TemplateException("未知类型, type=" + fieldType);
                    prop.put("type", dataTypeEnum.getJavaType());
                    prop.put("name", fieldName);
                    prop.put("note", fieldName);
                    prop.put("convertor", dataTypeEnum.getConvertor());
                    // Java实体信息
                    properties.add(prop);
                    // 拼接构造方法的参数
                    paramMethod.append(dataTypeEnum.getJavaType()).append(" ").append(fieldName);
                    if (i < sheetData.getNameList().size() - 1) {
                        paramMethod.append(", ");
                    }
                }
                resMap.put("paramMethod", paramMethod.toString());
                // 生成实体类
                this.genTemplateFile("TemplateJava.ftl", GenerateConfig.javaPath, entityName + ".java", resMap);

            }
        }

    }

    public void genTemplateFile(String tmplName, String exportPath, String fileName, Map<String, Object> resMap) throws Exception {
        genTemplateFile(getTemplate(tmplName), exportPath, fileName, resMap);
    }

    public void genTemplateFile(Template template, String exportPath, String fileName, Map<String, Object> resMap) throws Exception {
        writeFile(exportPath, fileName, resMap, template, StandardCharsets.UTF_8.name());
    }

    /**
     * 获取模板
     *
     * @param tmplName 模板文件名
     */
    private Template getTemplate(String tmplName) throws IOException {
        // Freemarker配置对象
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);

        // 获取模板文件的输入流 不能关闭
        InputStream templateStream = this.getClass().getClassLoader().getResourceAsStream("tmpl/" + tmplName);
        if (templateStream == null) {
            throw new TemplateException("模板 '" + tmplName + "' 不存在");
        }

        // 设置输入流加载器
        cfg.setTemplateLoader(new freemarker.cache.TemplateLoader() {
            @Override
            public Object findTemplateSource(String name) throws IOException {
                if (name.equals(tmplName)) {
                    return templateStream;
                }
                return null;
            }

            @Override
            public long getLastModified(Object templateSource) {
                return 0;
            }

            @Override
            public Reader getReader(Object templateSource, String encoding) throws IOException {
                return new InputStreamReader((InputStream) templateSource, encoding);
            }

            @Override
            public void closeTemplateSource(Object templateSource) throws IOException {
                // 不需要关闭 InputStream
            }
        });

        // 设置对象包装器
        cfg.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_31));
        cfg.setEncoding(Locale.getDefault(), StandardCharsets.UTF_8.name());

        // 使用的模板
        return cfg.getTemplate(tmplName);
    }

    /**
     * 生成文件
     *
     * @Author t13max
     * @Date 16:09 2024/8/6
     */
    private void writeFile(String filePath, String fileName, Map<String, Object> resMap, Template tmpl, String code) throws Exception {

        File file = FileUtil.newFile(filePath, fileName);

        // 写文件
        FileOutputStream outs = new FileOutputStream(file);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outs, code));
        tmpl.process(resMap, writer);
        writer.flush();
        writer.close();
    }

    /**
     * 生成json
     *
     * @Author t13max
     * @Date 15:37 2024/8/6
     */
    private void genJson() throws Exception {
        for (ExcelData excelData : generateContext.getExcelDataMap().values()) {
            for (SheetData sheetData : excelData.getSheetDataMap().values()) {
                List<Map<Object, Object>> dataMap = sheetData.getDataMap();
                String formatContent = JSON.toJSONString(dataMap, SerializerFeature.PrettyFormat,
                        SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat,
                        SerializerFeature.SortField, SerializerFeature.DisableCircularReferenceDetect);
                FileUtil.writeFile(GenerateConfig.jsonPath, sheetData.getSheetName(), "json", formatContent);
            }
        }
    }

    /**
     * 读取
     *
     * @Author t13max
     * @Date 16:15 2024/8/5
     */
    private void read() {
        this.generateContext = new GenerateContext();
        for (ExcelData excelData : generateContext.getExcelDataMap().values()) {
            EasyExcel.read(excelData.getFile().getAbsolutePath(), new TemplateReadListener(excelData)).headRowNumber(GenerateConfig.HEAD_COUNT).autoTrim(false).ignoreEmptyRow(true).doReadAll();
        }
    }

    /**
     * 打印环境信息
     *
     * @Author t13max
     * @Date 16:07 2024/8/5
     */
    private void printEnv() {

        Log.template.info("excelPath={}", GenerateConfig.excelPath);
        Log.template.info("javaPath={}", GenerateConfig.javaPath);
        Log.template.info("jsonPath={}", GenerateConfig.jsonPath);
    }

    /**
     * 清除老文件
     *
     * @Author t13max
     * @Date 16:14 2024/8/5
     */
    private void cleanUp() {

        //清除java类
        FileUtil.cleanFileBySuffix("java", GenerateConfig.javaPath);

        //清除json
        FileUtil.cleanFileBySuffix("json", GenerateConfig.jsonPath);
    }

}
