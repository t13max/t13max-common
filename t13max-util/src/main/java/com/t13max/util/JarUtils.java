package com.t13max.util;

import java.io.File;
import java.net.URI;

/**
 * @author t13max
 * @since 11:45 2025/2/25
 */
public class JarUtils {

    // 获取 JAR 所在目录
    public static String getJarDirectory() {
        String path = JarUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        try {
            URI uri = new URI(path);
            File jarFile = new File(uri);
            return jarFile.getParent(); // 获取 JAR 文件所在的目录
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
