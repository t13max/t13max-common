package com.t13max.agent.deployer;

import com.t13max.agent.wrap.ClassDefinitionWrap;
import com.t13max.agent.wrap.DelayClassDefinitionWrap;
import com.t13max.agent.wrap.Result;
import com.t13max.agent.util.Log;
import com.t13max.util.ClassUtil;
import com.t13max.util.ParseUtil;

import java.io.File;
import java.io.InputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 热部署
 *
 * @author: t13max
 * @since: 15:42 2024/8/12
 */
public abstract class HotDeployer {

    //结果
    private final Result result;
    //Instrumentation
    private final Instrumentation inst;
    //原本的类定义列表
    private final List<ClassDefinitionWrap> redefineOldClassesWrap = new ArrayList<>();
    //热更成新的类定义列表
    private final List<ClassDefinitionWrap> redefineNewClassesWrap = new ArrayList<>();
    //执行特定代码
    private final List<DelayClassDefinitionWrap> delayExecClassesWrap = new ArrayList<>();

    public HotDeployer(Result result, Instrumentation instrumentation) {
        this.result = result;
        this.inst = instrumentation;
    }

    /**
     * 获取热更包路径
     *
     * @Author t13max
     * @Date 15:50 2025/3/19
     */
    protected abstract String getHotDeployJarPath();

    /**
     * 是否必须
     *
     * @Author t13max
     * @Date 15:50 2025/3/19
     */
    protected boolean isRequired() {
        return true;
    }

    /**
     * 稍后执行类名列表
     *
     * @Author t13max
     * @Date 15:51 2025/3/19
     */
    protected List<String> getDelayExecClassNames() {
        return null;
    }

    /**
     * 执行
     *
     * @Author t13max
     * @Date 15:51 2025/3/19
     */
    public Result exec() {
        try {
            //读取jar文件
            File file = new File(this.getHotDeployJarPath());
            Log.agent.info(file.getAbsolutePath());
            //文件不存在
            if (!file.exists()) {
                //必须加载
                if (this.isRequired()) {
                    Log.agent.error("未找到必选jar包{}", this.getHotDeployJarPath());
                    this.result.setSuccess(false);
                } else {
                    this.result.setSuccess(true);
                }
                return this.result;
            }
            Log.agent.info("已找到jar包{}", this.getHotDeployJarPath());
            JarFile jarFile = new JarFile(file);
            Enumeration<JarEntry> jarEntry = jarFile.entries();

            String className;
            InputStream newStream;
            byte[] newBytes;
            //遍历
            while (jarEntry.hasMoreElements()) {
                JarEntry nextEntry = jarEntry.nextElement();
                String name = nextEntry.getName();
                //不是class文件 忽略
                if (!name.endsWith(".class")) continue;
                //包路径
                className = name.replace('/', '.');
                //把.class裁掉
                className = className.substring(0, className.lastIndexOf(46));
                //获取流
                newStream = jarFile.getInputStream(nextEntry);
                //获取二进制数组
                newBytes = ParseUtil.toBytes(newStream);
                try {
                    //拿到老class对象
                    Class<?> oldClazz = Class.forName(className);
                    byte[] oldBytes = null;
                    InputStream inputStream = oldClazz.getResourceAsStream("/" + name);
                    if (inputStream != null) {
                        oldBytes = ParseUtil.toBytes(inputStream);
                    }
                    //存在 那就是老的
                    this.redefineOldClassesWrap.add(new ClassDefinitionWrap(oldClazz, newBytes, oldBytes, className));
                } catch (ClassNotFoundException ignored) {
                    //不存在 那就是新加了一个类
                    this.redefineNewClassesWrap.add(new ClassDefinitionWrap(null, newBytes, null, className));
                }
            }

            for (ClassDefinitionWrap wrap : redefineNewClassesWrap) {
                className = wrap.getClassName();
                byte[] bytes = wrap.getNewBytes();
                if (!this.defineNewClass(className, bytes)) {
                    Log.agent.error("热加载(新类){}失败1{}", className);
                    this.result.setSuccess(false);
                    return this.result;
                }
                className = wrap.getClassName();
                Class<?> clazz;
                try {
                    clazz = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    Log.agent.error("热加载(新类){}失败3", className);
                    this.result.setSuccess(false);
                    return this.result;
                }
                try {
                    this.tryAppendDelayClassDefinitionWrap(clazz, null);
                } catch (Throwable throwable) {
                    Log.agent.error("热加载(新类){}失败4", className, throwable);
                    throw throwable;
                }
                this.result.appendMsg("热加载(新类)" + className + "成功");
            }

            for (ClassDefinitionWrap wrap : redefineOldClassesWrap) {
                Class<?> clazz = wrap.getClazz();
                className = wrap.getClassName();
                newBytes = wrap.getNewBytes();
                byte[] bytes = wrap.getOldBytes();
                try {
                    this.inst.redefineClasses(new ClassDefinition(clazz, newBytes));
                    this.tryAppendDelayClassDefinitionWrap(clazz, bytes);
                } catch (Throwable var12) {
                    Log.agent.error("热加载{}失败", className, var12);
                    throw var12;
                }
                Log.agent.info("热加载{}成功", className);
            }

            //执行稍后执行列表
            this.execDelayClass();
            Log.agent.info("热加载{}成功", this.getHotDeployJarPath());
            jarFile.close();
        } catch (Throwable var16) {
            Log.agent.error("热加载{}失败", this.getHotDeployJarPath(), var16);
            this.result.setSuccess(false);
        }

        return this.result;
    }

    private List<String> _getDelayExecClassNames() {
        List<String> list = this.getDelayExecClassNames();
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    /**
     * 尝试添加到稍后执行class
     *
     * @Author t13max
     * @Date 15:58 2025/3/19
     */
    private void tryAppendDelayClassDefinitionWrap(Class<?> clazz, byte[] bytes) {
        if (this._getDelayExecClassNames().contains(clazz.getName())) {
            this.delayExecClassesWrap.add(new DelayClassDefinitionWrap(clazz, bytes));
        }
    }

    /**
     * 稍后执行列表开始执行
     *
     * @Author t13max
     * @Date 15:59 2025/3/19
     */
    private void execDelayClass() throws Throwable {
        for (DelayClassDefinitionWrap wrap : this.delayExecClassesWrap) {
            this.execDelayClass(wrap);
        }
    }

    /**
     * 执行一个稍后执行class
     *
     * @Author t13max
     * @Date 15:59 2025/3/19
     */
    private void execDelayClass(DelayClassDefinitionWrap wrap) throws Throwable {

        Class<?> clazz = wrap.getClazz();

        try {
            Method method = clazz.getMethod("exec");
            method.invoke(null);
            //执行完后 恢复成老的class
            byte[] bytes = wrap.getBytes();
            if (bytes != null) {
                this.inst.redefineClasses(new ClassDefinition(clazz, bytes));
            }
            Log.agent.info("执行{}成功", clazz.getName());
        } catch (Throwable var5) {
            Log.agent.error("执行{}失败", clazz.getName());
            throw var5;
        }
    }

    /**
     * 根据二进制数组创建一个类
     *
     * @Author t13max
     * @Date 18:01 2024/8/12
     */
    private boolean defineNewClass(String className, byte[] bytes) {
        ClassLoader classLoader = this.getClass().getClassLoader();
        Method defineClassMethod = ClassUtil.getMethod(classLoader.getClass(), "defineClass", new Class[]{String.class, byte[].class, Integer.TYPE, Integer.TYPE});
        if (defineClassMethod == null) {
            Log.agent.error("ClassLoader未找到defineClass方法");
            return false;
        } else {
            defineClassMethod.setAccessible(true);
            try {
                defineClassMethod.invoke(classLoader, className, bytes, 0, bytes.length);
                return true;
            } catch (Throwable var6) {
                Log.agent.error("ClassLoader.defineClass执行错误", var6);
                return false;
            }
        }
    }
}
