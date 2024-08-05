package com.t13max.template.gen;

/**
 * 配置
 *
 * @author: t13max
 * @since: 16:04 2024/8/5
 */
public interface GenerateConfig {

    String excelPath = System.getProperty("excelPath");
    String javaPath = System.getProperty("javaPath");
    String jsonPath = System.getProperty("jsonPath");

    String ignore = "#";

    int HEAD_COUNT = 2;
}
