package com.t13max.util;

import java.io.File;

/**
 * 文件工具类
 *
 * @author: t13max
 * @since: 16:08 2024/8/5
 */
public class FileUtil {

    /**
     * 删除目录下指定后缀的文件
     *
     * @Author t13max
     * @Date 16:10 2024/8/5
     */
    public static int cleanFileBySuffix(String suffix, File folder) {
        int delFileNum = 0;
        if (!folder.exists()) {
            return delFileNum;
        }

        if (!folder.isDirectory()) {
            return delFileNum;
        }

        File[] files = folder.listFiles();
        if (files == null) {
            return delFileNum;
        }

        for (File file : files) {
            if (file.getName().endsWith(suffix)) {
                delete(file);
                delFileNum++;
            }
        }

        return delFileNum;
    }

    /**
     * 删除目录下指定后缀的文件
     *
     * @Author t13max
     * @Date 16:10 2024/8/5
     */
    public static int cleanFileBySuffix(String suffix, String path) {
        return cleanFileBySuffix(suffix, new File(path));
    }

    /**
     * 删除指定文件
     *
     * @Author t13max
     * @Date 16:11 2024/8/5
     */
    public static boolean delete(File file) {
        if (file == null) {
            return false;
        }

        if (file.isFile() && file.exists()) {
            boolean result = file.delete();
            if (!result) {
                return false;
            }
            return true;
        }

        return false;
    }

    /**
     * 删除文件
     *
     * @Author t13max
     * @Date 16:12 2024/8/5
     */
    public static boolean delete(String filePath) {
        return delete(new File(filePath));
    }
}
