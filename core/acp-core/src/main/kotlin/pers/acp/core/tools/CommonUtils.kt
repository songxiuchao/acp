package pers.acp.core.tools

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import kotlinx.coroutines.*
import org.apache.commons.lang3.StringUtils
import org.apache.commons.text.CharacterPredicates
import org.apache.commons.text.RandomStringGenerator
import org.joda.time.DateTime
import pers.acp.core.conf.AcpProperties
import pers.acp.core.log.LogFactory
import pers.acp.core.task.BaseAsyncTask
import pers.acp.core.task.timer.Calculation

import java.io.*
import java.net.URLDecoder
import java.nio.charset.Charset
import java.util.Properties
import java.util.UUID
import java.util.regex.Pattern
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * @author zhang by 08/07/2019
 * @since JDK 11
 */
object CommonUtils {

    private var pps: Properties? = null

    /**
     * 获取系统默认字符集
     *
     * @return 字符集
     */
    const val defaultCharset = "utf-8"

    var fontPath: String = "files/resource/font"

    private var deleteFileWaitTime: Long = 1200000L

    private var absPathPrefix: String = "abs:"

    private var userPathPrefix: String = "user:"

    private val log = LogFactory.getInstance(CommonUtils::class.java)

    fun init() {
        log.info("default charset is : $defaultCharset")
        absPathPrefix = getProperties("absPath.prefix", "abs:")
        userPathPrefix = getProperties("userPath.prefix", "user:")
        fontPath = getProperties("fonts.fold", "files/resource/font")
        deleteFileWaitTime = getProperties("deleteFile.waitTime", "1200000").toLong()
    }

    fun init(deleteFileWaitTime: Long, absPathPrefix: String, userPathPrefix: String, fontPath: String) {
        CommonUtils.deleteFileWaitTime = deleteFileWaitTime
        CommonUtils.absPathPrefix = absPathPrefix
        if (isNullStr(CommonUtils.absPathPrefix)) {
            CommonUtils.absPathPrefix = getProperties("absPath.prefix", "abs:")
        }
        CommonUtils.userPathPrefix = userPathPrefix
        if (isNullStr(CommonUtils.userPathPrefix)) {
            CommonUtils.userPathPrefix = getProperties("userPath.prefix", "user:")
        }
        CommonUtils.fontPath = fontPath
        if (isNullStr(CommonUtils.fontPath)) {
            CommonUtils.fontPath = getProperties("fonts.fold", "files/resource/font")
        }
    }

    /**
     * 初始化系统配置文件
     */
    private fun initSystemProperties() {
        try {
            pps = AcpProperties.getInstance()
        } catch (e: Exception) {
            log.error("load acp.properties failed!")
        }
    }

    /**
     * 获取 WebRoot 绝对路径
     *
     * @return WebRoot 绝对路径
     */
    fun getWebRootAbsPath(): String = try {
        var classPath = URLDecoder.decode(CommonUtils::class.java.getResource("/").path, defaultCharset)
        var indexWebInf = classPath.indexOf("WEB-INF")
        if (indexWebInf == -1) {
            indexWebInf = classPath.indexOf("bin")
        }
        var webRootPath = classPath
        if (indexWebInf != -1) {
            webRootPath = webRootPath.substring(0, indexWebInf)
        }
        when {
            webRootPath.startsWith("jar") -> webRootPath = webRootPath.substring(10)
            webRootPath.startsWith("file") -> webRootPath = webRootPath.substring(6)
            else -> {
                val classLoader = CommonUtils::class.java.classLoader
                val url = classLoader.getResource("/")
                url?.let {
                    classPath = it.path
                    indexWebInf = classPath.indexOf("WEB-INF")
                    if (indexWebInf == -1) {
                        indexWebInf = classPath.indexOf("bin")
                    }
                    webRootPath = if (indexWebInf != -1) {
                        classPath.substring(0, indexWebInf)
                    } else {
                        classPath
                    }
                }
            }
        }
        if (webRootPath.endsWith("/")) {
            webRootPath = webRootPath.substring(0, webRootPath.length - 1)
        }
        webRootPath = webRootPath.replace("/", File.separator)
        if (webRootPath.startsWith("\\")) {
            webRootPath = webRootPath.substring(1)
        }
        if (webRootPath.contains("!")) {
            webRootPath = webRootPath.substring(0, webRootPath.indexOf("!"))
            webRootPath = webRootPath.substring(0, webRootPath.lastIndexOf(File.separator))
        }
        if (isNullStr(webRootPath)) {
            webRootPath = File.separator
        } else {
            if (File.separator.equals("/", ignoreCase = true)) {
                if (!webRootPath.startsWith("/")) {
                    webRootPath = "/$webRootPath"
                }
            }
        }
        webRootPath
    } catch (e: Exception) {
        log.error(e.message, e)
        log.error("webRootAbsPath=\"\"")
        ""
    }


    /**
     * 获取36位全球唯一的字符串（带4个分隔符）
     *
     * @return 结果
     */
    fun getUuid(): String = UUID.randomUUID().toString().toUpperCase()

    /**
     * 获取uuid
     *
     * @param length 字符串长度
     * @return 结果
     */
    private fun getUuid(length: Int): String = RandomStringGenerator.Builder()
            .withinRange(33, 126)
            .filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS)
            .build().generate(length)

    /**
     * 获取32位全球唯一的字符串
     *
     * @return 结果
     */
    fun getUuid32(): String = getUuid().replace("-", "")

    /**
     * 获取24位短uuid
     *
     * @return 结果
     */
    fun getUuid24(): String = getUuid(24)

    /**
     * 获取16位短uuid
     *
     * @return 结果
     */
    fun getUuid16(): String = getUuid(16)

    /**
     * 获取8位短uuid
     *
     * @return 结果
     */
    fun getUuid8(): String = getUuid(8)

    /**
     * 获取当前时刻的 DateTime 实例
     *
     * @return DateTime 实例
     */
    fun getNowDateTime(): DateTime = DateTime()

    /**
     * 获取当前日期
     *
     * @return 日期字符串
     */
    fun getNowString(): String = getDateTimeString(dateTimeFormat = Calculation.DATE_FORMAT)

    /**
     * 获取当前时间
     *
     * @return 日期时间字符串
     */
    fun getNowTimeString(): String = getDateTimeString(dateTimeFormat = Calculation.DATETIME_FORMAT)

    /**
     * 获取指定格式的时间字符串
     *
     * @param dateTime       DateTime 实例
     * @param dateTimeFormat 格式
     * @return 格式化的时间格式
     */
    fun getDateTimeString(dateTime: DateTime? = null, dateTimeFormat: String = ""): String {
        val time = dateTime ?: getNowDateTime()
        var format = dateTimeFormat
        if (isNullStr(dateTimeFormat)) {
            format = Calculation.DATETIME_FORMAT
        }
        return time.toString(format)
    }

    /**
     * 获取资源文件的输入流
     *
     * @param fileName 文件路径
     * @return 输入流
     */
    fun getResourceInputStream(fileName: String): InputStream? {
        var name = fileName.replace("\\", "/")
        if (name.startsWith("/")) {
            name = name.substring(1)
        }
        return CommonUtils::class.java.classLoader.getResourceAsStream(name)
    }

    /**
     * 获取配置信息
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 值
     */
    fun getProperties(key: String, defaultValue: String = ""): String {
        if (pps == null) {
            initSystemProperties()
        }
        return if (pps != null) {
            pps!!.getProperty(key, defaultValue)
        } else {
            defaultValue
        }
    }

    /**
     * 判断路径是否是绝对路径
     *
     * @param path 路径
     * @return 是否是绝对路径
     */
    fun isAbsPath(path: String): Boolean {
        return path.startsWith(absPathPrefix)
    }

    /**
     * 获取绝对路径
     *
     * @param srcPath 路径
     * @return 绝对路径
     */
    fun getAbsPath(srcPath: String): String {
        val path = srcPath.replace("\\", File.separator).replace("/", File.separator)
        return when {
            isAbsPath(path) && path.startsWith(absPathPrefix) -> path.substring(absPathPrefix.length)
            !isAbsPath(path) && path.startsWith(userPathPrefix) -> System.getProperty("user.home") + path.substring(userPathPrefix.length)
            !isAbsPath(path) -> formatAbsPath(path)
            else -> path
        }
    }

    /**
     * 将相对路径格式化为绝对路径，相对于 java path
     *
     * @param path 相对路径
     * @return 绝对路径
     */
    fun formatAbsPath(path: String): String {
        var srcPath = path.replace("\\", File.separator).replace("/", File.separator)
        if (srcPath.startsWith(File.separator)) {
            srcPath = srcPath.substring(File.separator.length)
        }
        return File(srcPath).absolutePath
    }

    /**
     * 正则表达式匹配字符串
     *
     * @param regex  正则表达式
     * @param srcStr 待匹配字符串
     * @return true|false
     */
    fun regexPattern(regex: String, srcStr: String): Boolean {
        return !isNullStr(regex) && Pattern.compile(regex).matcher(srcStr).matches()
    }

    /**
     * 表达式变量替换
     *
     * @param varFormula 变量表达式:格式“${变量名}” 或带有变量格式的字符串
     * @param data       数据集
     * @return 目标字符串
     */
    fun replaceVar(varFormula: String, data: Map<String, String>): String {
        var formula = varFormula
        var tmpVar = varFormula
        if (tmpVar.contains("\${")) {
            if (tmpVar.contains("}")) {
                var begin = tmpVar.indexOf("\${")
                while (begin != -1) {
                    tmpVar = tmpVar.substring(begin + 2)
                    if (tmpVar.contains("}")) {
                        val varKey = tmpVar.substring(0, tmpVar.indexOf("}"))
                        if (data.containsKey(varKey)) {
                            formula = varFormula.replace("\${$varKey}", data.getOrElse(varKey) { "" })
                        }
                        tmpVar = tmpVar.substring(tmpVar.indexOf("}") + 1)
                        begin = tmpVar.indexOf("\${")
                    } else {
                        begin = -1
                    }
                }
            }
            return formula
        } else {
            return formula
        }
    }

    /**
     * 判断是否空字符串
     *
     * @param src 源字符串
     * @return 是否为空
     */
    fun isNullStr(src: String?): Boolean {
        return src.isNullOrBlank()
    }

    /**
     * 源字符串中每到指定长度时就插入子字符串
     *
     * @param src          源字符串
     * @param length       分隔长度
     * @param insertString 插入的子字符串
     * @return 目标字符串
     */
    fun autoInsertString(src: String, length: Int, insertString: String): String {
        var result = src
        val maxlength = src.length
        for (i in 0 until maxlength / length) {
            val endIndex = (i + 1) * length + i * insertString.length
            result = result.substring(0, endIndex) + insertString + result.substring(endIndex)
        }
        if (result.lastIndexOf(insertString) == result.length - insertString.length) {
            result = result.substring(0, result.length - insertString.length)
        }
        return result
    }

    /**
     * 字符串填充函数
     *
     * @param str    待填充的字符串
     * @param number 填充后的字节长度
     * @param flag   0-向左填充，1-向右填充
     * @param string 填充字符串
     * @return 填充后的字符串
     */
    fun strFillIn(str: String, number: Int, flag: Int, string: String): String {
        val byteLen = str.toByteArray().size
        val strLen = str.length
        return when (flag) {
            0 -> StringUtils.leftPad(str, number - (byteLen - strLen), string)
            1 -> StringUtils.rightPad(str, number - (byteLen - strLen), string)
            else -> ""
        }
    }

    /**
     * 判断字符串是否在数组中
     *
     * @param str        源字符串
     * @param array      字符串数组
     * @param ignoreCase 是否忽略大小写
     * @return 是否存在
     */
    fun strInArray(str: String, array: Array<String>, ignoreCase: Boolean = false): Boolean {
        for (anArray in array) {
            if (when {
                        ignoreCase && str.equals(anArray, ignoreCase = true) -> true
                        !ignoreCase && str == anArray -> true
                        else -> false
                    }) {
                return true
            }
        }
        return false
    }

    /**
     * 判断字符串是否在列表中
     *
     * @param str        源字符串
     * @param arrayList  字符串列表
     * @param ignoreCase 是否忽略大小写
     * @return 是否存在
     */
    fun strInList(str: String, arrayList: List<String>, ignoreCase: Boolean = false): Boolean =
            if (ignoreCase) {
                arrayList.stream().filter { anArrayList -> anArrayList.equals(str, ignoreCase = true) }.count() > 0
            } else {
                arrayList.stream().filter { anArrayList -> anArrayList == str }.count() > 0
            }


    /**
     * 字符串转JSON对象
     *
     * @param src 字符串
     * @return json对象
     */
    fun getJsonFromStr(src: String): JsonNode {
        val mapper = ObjectMapper()
        var jsonNode: JsonNode = mapper.createObjectNode()
        try {
            jsonNode = mapper.readTree(src)
        } catch (e: Exception) {
            log.error(e.message, e)
        }
        return jsonNode
    }

    /**
     * json对象转为java对象
     *
     * @param jsonObj                json对象（JsonNode）
     * @param cls                    目标类
     * @param propertyNamingStrategy 名称处理规则
     * @return 目标对象
     */
    fun <T> jsonToObject(jsonObj: JsonNode, cls: Class<T>, propertyNamingStrategy: PropertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE): T? {
        val mapper = ObjectMapper()
        var instance: T? = null
        try {
            mapper.propertyNamingStrategy = propertyNamingStrategy
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            instance = mapper.readValue(jsonObj.toString(), cls)
        } catch (e: IOException) {
            log.error(e.message, e)
        }
        return instance
    }

    /**
     * 实体对象转换为json对象
     *
     * @param instance               实体对象（只持Map对象）
     * @param propertyNamingStrategy 名称处理规则
     * @return json对象
     */
    fun objectToJson(instance: Any, propertyNamingStrategy: PropertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE): JsonNode {
        val jsonNodeFactory = JsonNodeFactory(true)
        val mapper = ObjectMapper()
        mapper.nodeFactory = jsonNodeFactory
        var jsonNode: JsonNode
        try {
            mapper.propertyNamingStrategy = propertyNamingStrategy
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
            jsonNode = getJsonFromStr(mapper.writeValueAsString(instance))
        } catch (e: JsonProcessingException) {
            log.error(e.message, e)
            jsonNode = mapper.createObjectNode()
        }

        return jsonNode
    }

    /**
     * 下划线转驼峰
     *
     * @param str 待处理字符串
     * @return 转换后的字符串
     */
    fun toCamel(str: String): StringBuffer {
        //利用正则删除下划线，把下划线后一位改成大写
        val pattern = Pattern.compile("_(\\w)")
        val matcher = pattern.matcher(str)
        var sb = StringBuffer(str)
        if (matcher.find()) {
            sb = StringBuffer()
            //将当前匹配子串替换为指定字符串，并且将替换后的子串以及其之前到上次匹配子串之后的字符串段添加到一个StringBuffer对象里。
            //正则之前的字符和被替换的字符
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase())
            //把之后的也添加到StringBuffer对象里
            matcher.appendTail(sb)
        } else {
            return sb
        }
        return toCamel(sb.toString())
    }


    /**
     * 驼峰转下划线
     *
     * @param str 待处理字符串
     * @return 转换后字符串
     */
    fun toUnderline(str: String): StringBuffer {
        val pattern = Pattern.compile("[A-Z]")
        val matcher = pattern.matcher(str)
        var sb = StringBuffer(str)
        if (matcher.find()) {
            sb = StringBuffer()
            //将当前匹配子串替换为指定字符串，并且将替换后的子串以及其之前到上次匹配子串之后的字符串段添加到一个StringBuffer对象里。
            //正则之前的字符和被替换的字符
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase())
            //把之后的也添加到StringBuffer对象里
            matcher.appendTail(sb)
        } else {
            return sb
        }
        return toUnderline(sb.toString())
    }

    /**
     * 在线程池中执行任务
     *
     * @param task       线程池任务
     * @return 协程任务
     */
    fun executeAsyncTask(task: BaseAsyncTask): Job =
            GlobalScope.launch {
                task.doExecute()
            }

    /**
     * 压缩文件
     *
     * @param fileNames      需要压缩的文件路径数组，可以是全路径也可以是相对于WebRoot的路径
     * @param resultFileName 生成的目标文件全路径
     * @param deleteFile     压缩完后是否删除原文件
     * @return 目标文件绝对路径
     */
    fun filesToZIP(fileNames: Array<String>, resultFileName: String, deleteFile: Boolean): String {
        val startTime = System.currentTimeMillis()
        var endTime: Long = 0
        var out: ZipOutputStream? = null
        try {
            val resultFileParent = File(resultFileName).parentFile
            if (!resultFileParent.exists()) {
                if (!resultFileParent.mkdirs()) {
                    log.error("mkdir failed : " + resultFileParent.absolutePath)
                }
            }
            out = ZipOutputStream(FileOutputStream(resultFileName))
            fileNames.forEach {
                val filename = it.replace("\\", File.separator).replace("/", File.separator)
                val srcFile = File(filename)
                compress(srcFile, out, srcFile.name)
                if (deleteFile) {
                    doDeleteFile(srcFile, false)
                }
            }
            out.close()
            log.info("compress success")
            endTime = System.currentTimeMillis()
            return resultFileName
        } catch (e: Exception) {
            try {
                out?.closeEntry()
                doDeleteFile(File(resultFileName), false)
            } catch (ex: Exception) {
                log.error("file compress Exception:" + ex.message, ex)
            }
            log.error("file compress Exception:" + e.message, e)
            endTime = System.currentTimeMillis()
            return ""
        } finally {
            log.info("time consuming : " + (endTime - startTime) + " ms")
        }
    }

    /**
     * 递归压缩
     *
     * @param sourceFile 源文件
     * @param zos        zip输出流
     * @param name       压缩后的名称
     * @throws Exception 异常
     */
    @Throws(Exception::class)
    private fun compress(sourceFile: File, zos: ZipOutputStream, name: String) {
        var input: FileInputStream? = null
        try {
            if (sourceFile.isFile) {
                val buf = ByteArray(1024)
                zos.putNextEntry(ZipEntry(name))
                input = FileInputStream(sourceFile)
                var len = input.read(buf)
                while (len != -1) {
                    zos.write(buf, 0, len)
                    len = input.read(buf)
                }
                zos.closeEntry()
                input.close()
                input = null
            } else {
                val listFiles = sourceFile.listFiles()
                if (listFiles == null || listFiles.isEmpty()) {
                    zos.putNextEntry(ZipEntry("$name/"))
                    zos.closeEntry()
                } else {
                    for (file in listFiles) {
                        compress(file, zos, name + "/" + file.name)
                    }
                }
            }
        } finally {
            input?.close()
        }
    }

    /**
     * 解压缩文件
     *
     * @param zipFileName  zip压缩文件名
     * @param parentFold   解压目标文件夹
     * @param deleteFile   解压完成是否删除压缩文件
     * @param charSet      字符编码
     */
    fun zipToFiles(zipFileName: String, parentFold: String, deleteFile: Boolean = false, charSet: String = defaultCharset) {
        val startTime = System.currentTimeMillis()
        var endTime: Long = 0
        var zin: ZipInputStream? = null
        var bin: BufferedInputStream? = null
        var bout: BufferedOutputStream? = null
        try {
            zin = ZipInputStream(FileInputStream(zipFileName), Charset.forName(charSet))
            bin = BufferedInputStream(zin)
            var fOut: File
            var entry: ZipEntry? = zin.nextEntry
            while (entry != null && !entry.isDirectory) {
                fOut = File(parentFold, entry.name)
                if (!fOut.exists()) {
                    if (!fOut.parentFile.mkdirs()) {
                        log.error("mkDirs failed : " + fOut.parent)
                    }
                }
                bout = BufferedOutputStream(FileOutputStream(fOut))
                var b: Int = bin.read()
                while (b != -1) {
                    bout.write(b)
                    b = bin.read()
                }
                bout.close()
                entry = zin.nextEntry
            }
            bin.close()
            zin.close()
            if (deleteFile) {
                doDeleteFile(File(zipFileName), false, 0)
            }
            log.info("decompress success")
            endTime = System.currentTimeMillis()
        } catch (e: Exception) {
            try {
                bout?.close()
                bin?.close()
                zin?.close()
            } catch (ex: Exception) {
                log.error("file decompress Exception:" + ex.message, ex)
            }
            log.error("file decompress Exception:" + e.message, e)
            endTime = System.currentTimeMillis()
        } finally {
            log.info("time consuming : " + (endTime - startTime) + " ms")
        }
    }

    /**
     * 删除文件
     *
     * @param file     待删除文件
     * @param isAsync   是否异步删除
     * @param waitTime 异步删除等待时间（单位毫秒）
     */
    fun doDeleteFile(file: File, isAsync: Boolean, waitTime: Long? = null) {
        if (isAsync) {
            var time = deleteFileWaitTime
            if (waitTime != null) {
                time = waitTime
            }
            GlobalScope.launch {
                log.info("ready delete file [" + file.absolutePath + "],waiting " + time / 1000 + " seconds")
                delay(deleteFileWaitTime)
                doDeleteFileOrDir(file)
            }
        } else {
            doDeleteFileOrDir(file)
        }
    }

    private fun doDeleteFileOrDir(file: File): Boolean {
        try {
            if (file.exists()) {
                if (file.isDirectory) {
                    val children = file.list()
                    if (children != null) {
                        for (aChildren in children) {
                            val success = doDeleteFileOrDir(File(file, aChildren))
                            if (!success) {
                                return false
                            }
                        }
                    }
                }
                return file.delete()
            } else {
                return true
            }
        } catch (e: Exception) {
            log.error("delete file Exception:" + e.message, e)
            return false
        }
    }

}