package com.t13max.template.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.t13max.common.exception.CommonException;
import com.t13max.template.ITemplate;
import com.t13max.util.TextUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.formula.functions.T;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@UtilityClass
public class JsonUtils {

    public <T extends ITemplate> List<T> readOutJson(String fileName, Class<T> clazz) {
        List<T> iTemplates = null;
        try {
            String string = TextUtil.readOutText(fileName);
            iTemplates = JSON.parseArray(string, clazz);
        } catch (Exception e) {
            throw new CommonException("读json转换对象失败, error={}" + e.getMessage());
        }
        return iTemplates;
    }

    public <T extends ITemplate> List<T> readInJarJson(String fileName, Class<T> clazz) {

        List<T> iTemplates = null;

        try {
            String string = TextUtil.readInJarText(fileName);
            iTemplates = JSON.parseArray(string, clazz);
        } catch (Exception e) {
            throw new CommonException("读json转换对象失败, error={}" + e.getMessage());
        }

        return iTemplates;
    }

    public JSONArray readJsonArray(String filaName) {

        JSONArray result = null;

        try {

            InputStream resourceAsStream = org.apache.logging.log4j.core.util.JsonUtils.class.getClassLoader().getResourceAsStream(filaName);
            if (resourceAsStream == null) {
                Log.template.error("JsonUtils, 加载{}失败", filaName);
                return new JSONArray();
            }

            InputStreamReader isr = new InputStreamReader(resourceAsStream);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder stringBuilder = new StringBuilder();
            String lineTxt = null;

            //将文件内容全部拼接到 字符串s
            while ((lineTxt = br.readLine()) != null) {
                stringBuilder.append(lineTxt);
            }
            result = JSON.parseArray(stringBuilder.toString());
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static <T> T readJsonFile(String fileName, Class<T> clazz) {
        T result = null;
        try {
            String string = TextUtil.readOutText(fileName);
            result = JSON.parseObject(string, clazz);
        } catch (Exception e) {
            throw new CommonException("读json转换对象失败, error={}" + e.getMessage());
        }
        return result;
    }
}