package com.t13max.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

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
     * 删除文件夹
     *
     * @Author t13max
     * @Date 16:11 2024/8/12
     */
    public static boolean deleteDir(String dir) {
        Path folderPath = Paths.get(dir);
        if (!Files.exists(folderPath) && !Files.isDirectory(folderPath)) {
            return false;
        }
        try {
            Files.walkFileTree(folderPath, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (!dir.equals(folderPath)) {
                        Files.delete(dir);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
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

    /**
     * 导出 Jar
     *
     * @Author t13max
     * @Date 18:33 2024/8/12
     */
    public static void toJar(String jarFilepath, OutputStream out, boolean keepDirStructure) throws Exception {
        JarOutputStream jos = null;
        var sourceFile = new File(jarFilepath);
        try {
            jos = new JarOutputStream(out);
            compress(sourceFile, jos, sourceFile.getName(), keepDirStructure);
        } finally {
            if (jos != null) {
                try {
                    jos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static final int BUFFER_SIZE = 2 * 1024;

    /**
     * 递归压缩
     *
     * @Author t13max
     * @Date 18:34 2024/8/12
     */
    public static void compress(File sourceFile, JarOutputStream jos, String name, boolean keepDirStructure) throws Exception {
        var buf = new byte[BUFFER_SIZE];
        if (sourceFile.isFile()) {
            // 向jar输出流中添加一个jar实体，构造器中name为jar实体的文件的名字
            jos.putNextEntry(new ZipEntry(name));
            // copy文件到jos输出流中
            int len;
            var in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1) {
                jos.write(buf, 0, len);
            }
            // Complete the entry
            jos.closeEntry();
            in.close();
        } else {
            var listFiles = sourceFile.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                // 需要保留原来的文件结构时,需要对空文件夹进行处理
                if (keepDirStructure) {
                    // 空文件夹的处理
                    jos.putNextEntry(new ZipEntry(name + "/"));
                    // 没有文件，不需要文件的copy
                    jos.closeEntry();
                }
            } else {
                for (File file : listFiles) {
                    // 判断是否需要保留原来的文件结构
                    // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                    // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                    compress(file, jos, (keepDirStructure ? name + "/" : "") + file.getName(), true);
                }
            }
        }
    }

    /**
     * 复制 Class 文件
     *
     * @Author t13max
     * @Date 18:35 2024/8/12
     */
    public static void copyClazzFile(File exportFolder, Class<?> clazz) throws Exception {
        var clazzBasePath = ClassUtil.getClazzAbsPath(clazz);
        var packageDir = clazz.getPackage().getName().replace('.', '/');
        var name = clazz.getName().substring(clazz.getName().lastIndexOf(".") + 1);

        var clazzSourceFile = new File(clazzBasePath + '/' + packageDir + '/' + name + ".class");
        var clazzCopyFile = new File(exportFolder.getAbsolutePath() + '/' + packageDir + '/' + name + ".class");
        if (!clazzCopyFile.getParentFile().exists() && !clazzCopyFile.getParentFile().mkdirs()) {
            throw new Exception("Class copy target path create failed. file = " + clazzCopyFile.getAbsolutePath());
        }

        if (!clazzCopyFile.createNewFile()) {
            throw new Exception("Class copy target file create failed. file = " + clazzCopyFile.getAbsolutePath());
        }

        Files.copy(clazzSourceFile.toPath(), clazzCopyFile.toPath());
    }

}
