package com.kernel.kotlintest

import pers.acp.core.CommonTools
import java.io.File

/**
 * Create by zhangbin on 2017-12-20 11:36
 */
object TestSimple {

    @JvmStatic
    fun main(args: Array<String>) {
        val dataBean1 = DataBean1(
                "你好123", 1, 0.34
        )
        val ss = CommonTools.objectToJson(dataBean1)
        println(ss.toString())
        val dataBean1_1 = CommonTools.jsonToObject(ss, DataBean1::class.java)
        println(dataBean1_1)


        val fold = "D:\\个人\\测试ftp"
        val files = File(fold).listFiles()?.map { file -> file.absolutePath } ?: listOf()
        CommonTools.filesToZip(files, "$fold\\testzip1.zip")
        CommonTools.filesToZip(files, "$fold\\testzip2.zip", password = "123")
        CommonTools.zipToFiles("$fold\\testzip1.zip", "$fold\\unzip1")
        CommonTools.zipToFiles("$fold\\testzip2.zip", "$fold\\unzip2", password = "123")
    }

}
