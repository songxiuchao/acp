package com.kernel.kotlintest

import pers.acp.core.tools.CommonUtils

/**
 * Create by zhangbin on 2017-12-20 11:36
 */
object TestSimple {

    @JvmStatic
    fun main(args: Array<String>) {
        val dataBean1 = DataBean1(
                "你好123", 1, 0.34
        )
        val ss = CommonUtils.objectToJson(dataBean1)
        println(ss.toString())
        val dataBean1_1 = CommonUtils.jsonToObject(ss, DataBean1::class.java)
        println(dataBean1_1)
    }

}
