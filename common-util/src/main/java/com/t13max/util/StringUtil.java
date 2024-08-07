package com.t13max.util;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 字符串工具类
 *
 * @Author t13max
 * @Date 18:27 2024/8/2
 */
@UtilityClass
public class StringUtil {

    public static final String SEMICOLON = ";";

    public static final String COMMA = ",";

    public static final String ASTERISK = "\\*";

    public static final String COLON = ":";

    /**
     * 首字母转小写
     *
     * @Author t13max
     * @Date 18:30 2024/8/2
     */
    public static String toLowerCaseFirstOne(final String s) {
        if (Character.isLowerCase(s.charAt(0))) {
            return s;
        } else {
            return Character.toLowerCase(s.charAt(0)) + s.substring(1);
        }
    }

    /**
     * 首字母转大写
     *
     * @Author t13max
     * @Date 18:30 2024/8/2
     */
    public static String toUpperCaseFirstOne(String s) {
        if (Character.isUpperCase(s.charAt(0))) {
            return s;
        } else {
            return Character.toUpperCase(s.charAt(0)) + s.substring(1);
        }
    }

    /**
     * 驼峰 转 下划线
     *
     * @Author t13max
     * @Date 18:30 2024/8/2
     */
    public static String camel2Underline(String line) {
        if (line == null || line.isEmpty()) {
            return "";
        }
        line = String.valueOf(line.charAt(0)).toUpperCase().concat(line.substring(1));
        StringBuilder sb = new StringBuilder();
        Pattern pattern = Pattern.compile("[A-Z]([a-z\\d]+)?");
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            String word = matcher.group();
            sb.append(word.toLowerCase());
            sb.append(matcher.end() == line.length() ? "" : "_");
        }
        return sb.toString();
    }

    public static List<Integer> getIntList(String str) {
        return getIntList(str, COMMA);
    }

    public static List<Integer> getIntList(String str, String separation) {
        return Arrays.stream(str.split(separation)).map(Integer::parseInt).toList();
    }

    public static List<Float> getFloatList(String str) {
        return getFloatList(str, COMMA);
    }

    public static List<Float> getFloatList(String str, String separation) {
        return Arrays.stream(str.split(separation)).map(Float::parseFloat).toList();
    }

    public static List<String> getStrList(String str) {
        return getStrList(str, COMMA);
    }

    public static List<String> getStrList(String str, String separation) {
        return Arrays.stream(str.split(separation)).toList();
    }

    public static Map<Integer, Integer> getIntMap(String str) {
        Map<Integer, Integer> result = new HashMap<>();
        List<String> strList = getStrList(str);
        for (String kv : strList) {
            String[] split = kv.split(ASTERISK);
            if (split.length != 2) {
                continue;
            }
            result.put(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
        }
        return result;
    }
}
