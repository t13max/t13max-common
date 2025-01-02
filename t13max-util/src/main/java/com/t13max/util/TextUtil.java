package com.t13max.util;

import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 文本工具类
 *
 * @author: t13max
 * @since: 11:36 2024/5/29
 */
@UtilityClass
public class TextUtil {

    public String readOutText(String fileName) {
        String content;
        try {
            // 读取文件内容为字符串
            content = new String(Files.readAllBytes(Paths.get(fileName)));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return content;
    }

    public String readInJarText(String fileName) {
        return readIgnore(fileName, null);
    }

    public String readSql(String fileName, ClassLoader classLoader) {
        return readIgnore(fileName, "--", classLoader);
    }

    public String readSql(String fileName) {
        return readIgnore(fileName, "--", Thread.currentThread().getContextClassLoader());
    }

    public String readIgnore(String fileName, String ignore, ClassLoader classLoader) {
        InputStream resourceAsStream = classLoader.getResourceAsStream(fileName);
        if (resourceAsStream == null) {
            throw new RuntimeException("资源找不到");
        }
        InputStreamReader isr = new InputStreamReader(resourceAsStream);
        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader br = new BufferedReader(isr)) {

            String lineTxt = null;

            //将文件内容全部拼接到 字符串s
            while ((lineTxt = br.readLine()) != null) {
                if (ignore != null && lineTxt.contains(ignore)) {
                    continue;
                }
                stringBuilder.append(lineTxt);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return stringBuilder.toString();
    }

    public String readIgnore(String fileName, String ignore) {
        return readIgnore(fileName, ignore, Thread.currentThread().getContextClassLoader());
    }
}
