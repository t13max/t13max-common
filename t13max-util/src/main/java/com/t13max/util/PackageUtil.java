package com.t13max.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 包扫描工具类
 *
 * @author: t13max
 * @since: 14:15 2024/5/23
 */
public class PackageUtil {

    //缓存 防止重复扫描
    private static final Map<String, Set<Class<?>>> CACHE = new HashMap<>();

    /**
     * 扫描类 使用指定类加载器
     *
     * @Author t13max
     * @Date 14:18 2024/5/23
     */
    public static Set<Class<?>> scan(String packagePattern, ClassLoader classLoader) {
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        Set<Class<?>> classes = new LinkedHashSet<>();

        boolean recursive = true;

        if (packagePattern.contains("*")) {
            // 拆分 prefix 和 suffix
            String prefix = packagePattern.substring(0, packagePattern.indexOf('*')).replace('.', '/');
            String suffix = packagePattern.substring(packagePattern.indexOf('*') + 1).replace('.', '/');

            try {
                Enumeration<URL> dirs = classLoader.getResources(prefix);
                while (dirs.hasMoreElements()) {
                    URL url = dirs.nextElement();
                    if ("file".equals(url.getProtocol())) {
                        String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                        findClassesWithWildcard(prefix, suffix, filePath, classes, classLoader);
                    } else if ("jar".equals(url.getProtocol())) {
                        findClassesWithWildcardInJar(url, prefix, suffix, classes, classLoader);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            String packageDirName = packagePattern.replace('.', '/');
            try {
                Enumeration<URL> dirs = classLoader.getResources(packageDirName);
                while (dirs.hasMoreElements()) {
                    URL url = dirs.nextElement();
                    if ("file".equals(url.getProtocol())) {
                        String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                        findAndAddClassesInPackageByFile(packagePattern, filePath, recursive, classes);
                    } else if ("jar".equals(url.getProtocol())) {
                        findAndAddClassesInPackageByJar(classLoader, url, packageDirName, packagePattern, recursive, classes);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return classes;
    }

    private static void findClassesWithWildcard(String prefix, String suffix, String filePath, Set<Class<?>> classes, ClassLoader classLoader) {
        File dir = new File(filePath);
        if (!dir.exists() || !dir.isDirectory()) return;
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (File subDir : files) {
            if (!subDir.isDirectory()) continue;

            // 拼接完整包路径
            String middle = subDir.getName();
            File targetDir = new File(subDir, suffix);
            if (targetDir.exists() && targetDir.isDirectory()) {
                File[] targetFiles = targetDir.listFiles();
                if (targetFiles == null) {
                    return;
                }
                for (File file : targetFiles) {
                    if (file.getName().endsWith(".class")) {
                        String className = prefix.replace('/', '.') + middle + suffix.replace('/', '.')
                                + "." + file.getName().replace(".class", "");
                        try {
                            classes.add(Class.forName(className, true, classLoader));
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private static void findClassesWithWildcardInJar(URL url, String prefix, String suffix, Set<Class<?>> classes, ClassLoader classLoader) {
        try {
            JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.endsWith(".class") && name.startsWith(prefix) && name.contains(suffix)) {
                    String className = name.replace('/', '.').replace(".class", "");
                    try {
                        classes.add(Class.forName(className, true, classLoader));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 以jar的形式来获取包下的所有Class
     *
     * @Author t13max
     * @Date 14:18 2024/5/23
     */
    private static void findAndAddClassesInPackageByJar(ClassLoader classLoader, URL url, String packageDirName, String packageName, boolean recursive, Set<Class<?>> classes) {
        JarFile jar;
        try {
            jar = ((JarURLConnection) url.openConnection()).getJarFile();
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.charAt(0) == '/') {
                    name = name.substring(1);
                }
                if (name.startsWith(packageDirName)) {
                    int idx = name.lastIndexOf('/');
                    if (idx != -1) {
                        packageName = name.substring(0, idx).replace('/', '.');
                    }
                    if ((idx != -1) || recursive) {
                        if (name.endsWith(".class") && !entry.isDirectory()) {
                            String className = name.substring(packageName.length() + 1, name.length() - 6);
                            try {
                                classes.add(Class.forName(packageName + '.' + className, true, classLoader));
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 扫描类 默认使用Thread.currentThread().getContextClassLoader()类加载器
     *
     * @Author: t13max
     * @Since: 22:29 2024/7/19
     */
    public static Set<Class<?>> scan(String pack) {
        return scan(pack, null);
    }

    /**
     * 扫描并检查缓存
     * 如果某些类需要经常被扫 就应该缓存下来 下次直接获取
     *
     * @Author t13max
     * @Date 17:02 2024/8/2
     */
    public static Set<Class<?>> scanCache(String pack) {
        Set<Class<?>> classes = CACHE.get(pack);
        if (classes != null) return classes;
        classes = scan(pack);
        CACHE.put(pack, classes);
        return classes;
    }


    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @Author t13max
     * @Date 14:18 2024/5/23
     */
    public static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, Set<Class<?>> classes) {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] dirfiles = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });
        for (File file : dirfiles) {
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classes);
            } else {
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        Set<Class<?>> scan = scan("com.t13max.util.*.rpc");
        System.out.println(scan);
    }
}
