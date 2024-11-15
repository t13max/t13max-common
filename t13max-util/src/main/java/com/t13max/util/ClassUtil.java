package com.t13max.util;

import lombok.experimental.UtilityClass;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

/**
 * @author: t13max
 * @since: 17:45 2024/8/12
 */
@UtilityClass
public class ClassUtil {

    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... param) {
        Method method = null;

        while (clazz != Object.class) {
            try {
                method = clazz.getDeclaredMethod(methodName, param);
                break;
            } catch (NoSuchMethodException var6) {
                clazz = clazz.getSuperclass();
            }
        }

        return method;
    }

    /**
     * 获得 Class 绝对路径
     */
    public static String getClazzAbsPath(Class<?> clazz) throws Exception {
        if (clazz == null) {
            throw new Exception("入参错误, 类为空.");
        }

        StringBuilder path = new StringBuilder();

        var loader = clazz.getClassLoader();
        // 获得类的全名，包括包名
        var clazzName = clazz.getName() + ".class";
        // 获得传入参数所在的包
        var pack = clazz.getPackage();
        // 如果不是匿名包，将包名转化为路径
        if (pack != null) {
            var packName = pack.getName();

            // 是否是 Java 基础类库，防止用户传入JDK内置的类库
            if (packName.startsWith("java.") || packName.startsWith("javax.")) {
                throw new Exception("禁止操作Java基础类库.");
            }

            // 在类的名称中，去掉包名的部分，获得类的文件名
            clazzName = clazzName.substring(packName.length() + 1);

            // 简单包名则直接将包名转换为路径，
            if (!packName.contains(".")) {
                path = new StringBuilder(packName + "/");
            } else {
                // 否则按照包名的组成部分，将包名转换为路径
                int start = 0, end = packName.indexOf(".");
                while (end != -1) {
                    path.append(packName, start, end).append("/");
                    start = end + 1;
                    end = packName.indexOf(".", start);
                }
                path.append(packName.substring(start)).append("/");
            }
        }

        // 调用 ClassLoader 的 getResource 方法，传入包含路径信息的类文件名
        var url = loader.getResource(path + clazzName);
        if (url == null) {
            throw new Exception("类URL为空. className = " + clazz.getName());
        }

        // 从URL对象中获取路径信息
        var realPath = url.getPath();

        // 去掉路径信息中的协议名 "file:"
        int pos = realPath.indexOf("file:");
        if (pos > -1) {
            realPath = realPath.substring(pos + 5);
        }

        // 去掉路径信息最后包含类文件信息的部分，得到类所在的路径
        pos = realPath.indexOf(path + clazzName);
        realPath = realPath.substring(0, pos - 1);

        // 如果类文件被打包到 JAR 等文件中时，去掉对应的 JAR 等打包文件名
        if (realPath.endsWith("!")) {
            realPath = realPath.substring(0, realPath.lastIndexOf("/"));
        }

        try {
            realPath = java.net.URLDecoder.decode(realPath, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return realPath;
    }
}
