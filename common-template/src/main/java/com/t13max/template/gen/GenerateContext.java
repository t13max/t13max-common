package com.t13max.template.gen;

import com.t13max.template.exception.TemplateException;
import com.t13max.util.FileUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 上下文 缓存文件 excel 等信息
 *
 * @author: t13max
 * @since: 16:03 2024/8/5
 */
@Getter
@Setter
public class GenerateContext {

    private Map<String, ExcelData> excelDataMap = new HashMap<>();

    public GenerateContext() {
        File folder = new File(GenerateConfig.excelPath);
        if (!folder.isDirectory()) {
            throw new TemplateException("excelPath有误, path=" + GenerateConfig.excelPath);
        }

        //读取一个文件夹
        readFolder(folder);
    }

    /**
     * 读取某个文件夹
     *
     * @Author t13max
     * @Date 15:32 2024/8/6
     */
    private void readFolder(File folder) {
        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            throw new TemplateException("excelPath有误, 文件夹为空, path=" + GenerateConfig.excelPath);
        }
        for (File file : files) {
            if (FileUtil.isExcel(file)) {
                ExcelData excelData = new ExcelData(file.getName(), file);
                excelDataMap.put(file.getName(), excelData);
            } else if (file.isDirectory()) {
                readFolder(file);
            }
        }
    }
}
