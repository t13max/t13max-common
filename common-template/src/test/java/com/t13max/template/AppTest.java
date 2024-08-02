package com.t13max.template;

import com.alibaba.excel.EasyExcel;
import com.t13max.common.manager.ManagerBase;
import com.t13max.template.entity.HeroTemplate;
import com.t13max.template.listener.ReadDataListener;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {

        ManagerBase.initAllManagers();

        // 写入数据到Excel文件
    }
}
