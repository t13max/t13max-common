package com.t13max.template.gen;

import com.t13max.template.util.Log;
import com.t13max.util.FileUtil;

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

        //先清除
        cleanUp();

        //读取
        read();

        //生成json

        //生成java

    }

    /**
     * 读取
     *
     * @Author t13max
     * @Date 16:15 2024/8/5
     */
    private void read() {
        this.generateContext = new GenerateContext();

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
