package com.t13max.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * 文件工具类
 *
 * @author: t13max
 * @since: 16:08 2024/8/5
 */
public class FileUtil {

    /**
     * 生成一个文件，自动创建目录且删除旧文件
     *
     * @Author t13max
     * @Date 15:51 2024/8/6
     */
    public static File newFile(String filePath, String fileName) throws Exception {

        // 路径文件
        File path = getPathFile(filePath);
        if (path == null) return null;

        // 生成文件
        File file = Path.of(path.getPath(), fileName).toFile();
        if (file.exists()) {
            // 删除文件
            FileUtil.delete(file);
        }

        if (!file.createNewFile()) {
            return null;
        }

        return file;
    }

    public static File newFile(String filePath, String fileName, String suffix) throws Exception {
        if (!suffix.startsWith(".")) {
            suffix = "." + suffix;
        }

        return newFile(filePath, fileName + suffix);
    }

    /**
     * 写入文件内容
     *
     * @Author t13max
     * @Date 15:51 2024/8/6
     */
    public static File writeFile(File file, String content) throws Exception {
        if (file == null) {
            return null;
        }

        if (!file.exists() || !file.isFile()) {
            return null;
        }

        // 写文件
        FileOutputStream outs = new FileOutputStream(file);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outs, StandardCharsets.UTF_8));
        writer.write(content);
        writer.close();
        return file;
    }

    public static File writeFile(String filePath, String fileName, String suffix, String content) throws Exception {
        return writeFile(newFile(filePath, fileName, suffix), content);
    }

    public static File writeFile(String filePath, String fileName, String content) throws Exception {
        return writeFile(newFile(filePath, fileName), content);
    }

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

    /**
     * 文件为excel文件
     *
     * @Author t13max
     * @Date 15:26 2024/8/6
     */
    public static boolean isExcel(File file) {
        // 文件为路径
        if (!file.exists() || file.isDirectory() || !file.isFile() || file.isHidden() || !file.canRead()) {
            return false;
        }

        String fileName = file.getName();
        // 忽略文件
        if (file.getPath().contains("~$") || fileName.startsWith("#")) {
            return false;
        }

        if (!fileName.endsWith(".xlsx")) {
            return false;
        }

        return true;
    }

    /**
     * 获取目录
     *
     * @Author t13max
     * @Date 15:48 2024/8/6
     */
    public synchronized static File getPathFile(String filePath) {
        File path = new File(filePath);
        if (!path.exists()) {
            if (!path.mkdirs()) {
                return null;
            }
        }
        return path;
    }
}
