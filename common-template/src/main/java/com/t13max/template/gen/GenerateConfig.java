package com.t13max.template.gen;

/**
 * 配置
 * <p>
 * excelPath=/Users/antingbi/IdeaProjects/t13max-common/common-template/src/test/resources;javaPath=/Users/antingbi/mc/gen/java;jsonPath=/Users/antingbi/mc/gen/json;packageName=com.t13max.template
 *
 * @author: t13max
 * @since: 16:04 2024/8/5
 */
public interface GenerateConfig {

    String excelPath = System.getenv("excelPath");
    String javaPath = System.getenv("javaPath");
    String jsonPath = System.getenv("jsonPath");
    String packageName = System.getenv("packageName");

    String ignore = "#";

    int HEAD_COUNT = 2;
}
