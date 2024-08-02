package com.t13max.template.gen;

import com.alibaba.excel.EasyExcel;
import com.t13max.template.listener.ReadHeadListener;
import com.t13max.template.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: t13max
 * @since: 13:42 2024/8/2
 */
public class TemplateGenerator {

    public static void main(String[] args) {
        String excelPath = args[0];
        String outPutPath = args[1];
        File directory = new File(excelPath);
        if (!directory.isDirectory()) {
            Log.template.error("输入路径错误, path={}", excelPath);
            return;
        }
        File[] files = directory.listFiles();
        if (files == null) {
            Log.template.error("文件夹内一个文件都没有, path={}", excelPath);
            return;
        }

        List<ReadHeadListener> generateList = new ArrayList<>();
        for (File file : files) {
            if (file.isDirectory()) continue;
            ReadHeadListener readHeadListener = new ReadHeadListener();
            EasyExcel.read(file, readHeadListener).sheet().doRead();
            generateList.add(readHeadListener);
        }

        generate(generateList, outPutPath);
    }

    private static void generate(List<ReadHeadListener> generateList, String outPutPath) {

    }


}
