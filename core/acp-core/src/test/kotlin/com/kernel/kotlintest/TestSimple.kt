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

        testDownLoad()

//        val fold = "D:\\个人\\测试ftp"
//        val files = File(fold).listFiles()?.map { file -> file.canonicalPath } ?: listOf()
//        CommonTools.filesToZip(files, "$fold\\testzip1.zip")
//        CommonTools.filesToZip(files, "$fold\\testzip2.zip", password = "123")
//        CommonTools.zipToFiles("$fold\\testzip1.zip", "$fold\\unzip1")
//        CommonTools.zipToFiles("$fold\\testzip2.zip", "$fold\\unzip2", password = "123")
    }

    fun testDownLoad() {
        val path = CommonTools.getWebRootAbsPath() + "/files/tmp/download".replace("/", File.separator).replace("\\", File.separator)
        val filterRegex: MutableList<String> = mutableListOf()
        filterRegex.addAll(mutableListOf(
                CommonTools.getWebRootAbsPath() + "${File.separator}files${File.separator}tmp${File.separator}.*",
                CommonTools.getWebRootAbsPath() + "${File.separator}files${File.separator}upload${File.separator}.*",
                CommonTools.getWebRootAbsPath() + "${File.separator}files${File.separator}download${File.separator}.*"))
        println(pathFilter(filterRegex, path))
    }

    /**
     * 文件路径过滤
     *
     * @param filterRegex 路径
     * @param path        待匹配路径
     * @return true-允许下载 false-不允许下载
     */
    private fun pathFilter(filterRegex: List<String>, path: String): Boolean {
        path.apply {
            for (regex in filterRegex) {
                if (CommonTools.regexPattern(regex.replace("\\", "/"), this.replace("\\", "/"))) {
                    return true
                }
            }
        }
        return false
    }

}
