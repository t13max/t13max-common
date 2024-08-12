package com.t13max.common.util;

import com.t13max.util.FileUtil;

import java.io.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * @Author yanisChen
 * @Date 2022/11/30 1:43
 * @Description 打包热更新类工具
 */
public class GenHotDeployUtil {

    public static void main(String[] args) {
        // 启动参数打包热更新类
        Set<String> clazzNames = new HashSet<>();
        Set<Class<?>> clazzSet = new HashSet<>();
        if (args.length > 0) {
            clazzNames.addAll(Arrays.asList(args));
        }

        // ---------------------------------------------------------------------
        // 手动添加要热更新的类
        // ---------------------------------------------------------------------


        // ---------------------------------------------------------------------

        // 导出路径
        var exportDir = System.getProperty("exportPath", "reload");
        var genUtil = new GenHotDeployUtil();
        try {
            genUtil.execute(exportDir, clazzNames, clazzSet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.exit(0);
    }

    /**
     * 执行打包
     */
    public void execute(String baseDir, Set<String> clazzNames, Set<Class<?>> clazzSet) throws Exception {
        long start = System.currentTimeMillis();

        LinkedBlockingQueue<String> clazzQueue = new LinkedBlockingQueue<>(clazzNames);
        if (clazzSet != null && !clazzSet.isEmpty()) {
            clazzQueue.addAll(clazzSet.stream().map(Class::getName).collect(Collectors.toSet()));
        }

        if (clazzQueue.isEmpty()) {
            throw new Exception("热更类为空");
        }

        var exportFolder = new File(baseDir);
        if (exportFolder.exists()) {
            FileUtil.deleteDir(baseDir);
        }

        if (!exportFolder.exists() && !exportFolder.mkdirs()) {
            throw new Exception("创建文件夹失败, folder = " + exportFolder.getAbsolutePath());
        }

        List<String> exports = new ArrayList<>();
        while (!clazzQueue.isEmpty()) {
            var clazzName = clazzQueue.poll();
            Class<?> clazz = Class.forName(clazzName);

            // 内部类
            clazzQueue.addAll(Arrays.stream(clazz.getDeclaredClasses()).map(Class::getName).toList());

            // 匿名内部类
            int i = 1;
            while (true) {
                Class<?> innerClazz;
                try {
                    innerClazz = Class.forName(clazzName + "$" + i++);
                } catch (Exception ex) {
                    // 未找到匿名内部类
                    break;
                }

                clazzQueue.add(innerClazz.getName());
            }

            // 拷贝类文件
            FileUtil.copyClazzFile(exportFolder, clazz);
            exports.add(clazz.getName());
        }

        var jarFile = new File(exportFolder.getAbsolutePath() + "/hotdeploy.jar");
        if (jarFile.exists() && !jarFile.delete()) {
            throw new Exception("老jar删除失败. jarFile = " + jarFile.getAbsolutePath());
        }

        if (!jarFile.createNewFile()) {
            throw new Exception("Jar创建失败 file = " + jarFile.getAbsolutePath());
        }

        FileOutputStream jos = new FileOutputStream(jarFile);
        FileUtil.toJar(exportFolder.getAbsolutePath() + "/com", jos, true);

        System.out.println("导出类文件: " + exports.size() + "个");
        exports.forEach(System.out::println);
        System.out.println("打包共耗时: " + (System.currentTimeMillis() - start) + "ms");
        System.out.println("热更新 Jar 所在路径:\t" + jarFile.getParentFile().getAbsolutePath());
        System.out.println("热更新 Jar 文件:\t\t" + jarFile.getAbsolutePath());
    }


}
