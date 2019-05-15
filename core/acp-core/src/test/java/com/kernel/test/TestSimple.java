package com.kernel.test;

import pers.acp.core.CommonTools;
import pers.acp.core.task.threadpool.ThreadPoolService;
import pers.acp.core.task.threadpool.basetask.BaseThreadTask;

import java.math.BigInteger;

/**
 * Create by zhangbin on 2017-11-22 20:06
 */
public class TestSimple {

    public static void main(String[] args) throws InterruptedException {
        BigInteger bigInteger1 = new BigInteger("9223372036854775807");
        BigInteger bigInteger2 = BigInteger.valueOf(Long.valueOf("9223372036854775807"));
        System.out.println("bigInteger1=" + bigInteger1);
        System.out.println("bigInteger1=" + bigInteger1.longValue());
        System.out.println("bigInteger2=" + bigInteger2);
        System.out.println("bigInteger2=" + bigInteger2.longValue());

        testPath();
        testThreadPool();
    }

    private static void testThreadPool() throws InterruptedException {
        ThreadPoolService threadPoolService = ThreadPoolService.getInstance(10, Integer.MAX_VALUE, 10);
//        for (int i = 0; i < 10000; i++) {
//            threadPoolService.addTask(new BaseThreadTask(i + "") {
//                @Override
//                public boolean beforeExcuteFun() {
//                    return true;
//                }
//
//                @Override
//                public Object excuteFun() {
//                    System.out.println("i=" + this.getTaskName());
//                    return true;
//                }
//
//                @Override
//                public void afterExcuteFun(Object result) {
//
//                }
//            });
//        }
        BaseThreadTask task = new BaseThreadTask("") {
            @Override
            public boolean beforeExcuteFun() {
                return true;
            }

            @Override
            public Object excuteFun() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "123";
            }

            @Override
            public void afterExcuteFun(Object result) {

            }
        };
        System.out.println(CommonTools.excuteTaskInThreadPool(threadPoolService, task));
    }

    private static void testPath() {
        System.out.println(System.getProperty("user.home"));
        System.out.println(CommonTools.getWebRootAbsPath());
        System.out.println(CommonTools.formatAbsPath("/logs"));
    }

}
