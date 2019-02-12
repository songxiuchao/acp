package com.kernel.test;

import pers.acp.core.CommonTools;
import pers.acp.core.task.threadpool.ThreadPoolService;
import pers.acp.core.task.threadpool.basetask.BaseThreadTask;

import java.io.File;
import java.math.BigInteger;

/**
 * Create by zhangbin on 2017-11-22 20:06
 */
public class TestSimple {

    public static void main(String[] args) {
        BigInteger bigInteger1 = new BigInteger("9223372036854775807");
        BigInteger bigInteger2 = BigInteger.valueOf(Long.valueOf("9223372036854775807"));
        System.out.println("bigInteger1=" + bigInteger1);
        System.out.println("bigInteger1=" + bigInteger1.longValue());
        System.out.println("bigInteger2=" + bigInteger2);
        System.out.println("bigInteger2=" + bigInteger2.longValue());

        testPath();
    }

    private static void testThreadPool() {
        ThreadPoolService poolService = ThreadPoolService.getInstance(3000, 50);
        for (int i = 0; i < 100000; i++) {
            poolService.addTask(new BaseThreadTask(i + "") {
                @Override
                public boolean beforeExcuteFun() {
                    return true;
                }

                @Override
                public Object excuteFun() {
                    System.out.println(this.getThreadindex() + " i=" + this.getTaskName());
                    return true;
                }

                @Override
                public void afterExcuteFun(Object result) {

                }
            });
        }
    }

    private static void testPath() {
        System.out.println(System.getProperty("user.home"));
        System.out.println(CommonTools.getWebRootAbsPath());
        System.out.println(CommonTools.formatAbsPath("/logs"));
    }

}
