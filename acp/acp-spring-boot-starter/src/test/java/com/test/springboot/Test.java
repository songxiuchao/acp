package com.test.springboot;

import pers.acp.springboot.core.tools.ServletTools;

/**
 * @author zhangbin by 13/04/2018 11:46
 * @since JDK1.8
 */
public class Test {

    public static void main(String[] args) {
        String url = "/oauth/token1213";
        String regex = "/oauth/token\\d*";
        System.out.println(ServletTools.isBeIdentifiedUri(url,regex));
    }

}
