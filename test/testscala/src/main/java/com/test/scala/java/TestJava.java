package com.test.scala.java;

/**
 * Create by zhangbin on 2017-9-28 16:38
 */
public class TestJava {

    public static String test1(String p1, int p2) {
        return p1 + p2;
    }

    /**
     * 你猜
     *
     * @param p1 1
     * @param p2 1
     * @return r
     */
    public int test2(int p1, int p2) {
        return p1 * p2;
    }

    public void testForFunc(int count) {
        int a = 0;
        for (int i = 0; i < count; i++) {
            a += i;
        }
    }

    public void testWhileFunc(int count) {
        int a = 0;
        int i = 0;
        while (i < count) {
            a += i;
            i++;
        }
    }

}
