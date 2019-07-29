package pers.acp.spring.boot.component

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.core.CommonTools
import pers.acp.core.log.LogFactory
import pers.acp.spring.boot.interfaces.LogAdapter

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.io.*
import java.net.URLEncoder

/**
 * 文件下载处理组件
 *
 * @author zhangbin by 2018-1-21 1:30
 * @since JDK 11
 */
@Component
class FileDownLoadHandle @Autowired
constructor(private val log: LogAdapter) {

    @Throws(ServerException::class)
    @JvmOverloads
    fun downLoadForWeb(request: HttpServletRequest, response: HttpServletResponse, path: String, isDelete: Boolean, allowPathRegexList: List<String>? = null) {
        var filePath = path.replace("/", File.separator).replace("\\", File.separator)
        if (!filePath.startsWith(File.separator)) {
            filePath = File.separator + filePath
        }
        val webRootPath = CommonTools.getWebRootAbsPath()
        if (webRootPath != File.separator) {
            filePath = webRootPath + filePath
        }
        downLoadFile(request, response, filePath, isDelete, allowPathRegexList)
    }

    @Throws(ServerException::class)
    @JvmOverloads
    fun downLoadFile(request: HttpServletRequest, response: HttpServletResponse, filePath: String, isDelete: Boolean, allowPathRegexList: List<String>? = null) {
        val path = filePath.replace("/", File.separator).replace("\\", File.separator)
        val filterRegex: MutableList<String> = mutableListOf()
        if (allowPathRegexList == null || allowPathRegexList.isEmpty()) {
            filterRegex.addAll(mutableListOf(
                    CommonTools.getWebRootAbsPath() + "${File.separator}files${File.separator}tmp${File.separator}.*",
                    CommonTools.getWebRootAbsPath() + "${File.separator}files${File.separator}upload${File.separator}.*",
                    CommonTools.getWebRootAbsPath() + "${File.separator}files${File.separator}download${File.separator}.*"))
        } else {
            filterRegex.addAll(allowPathRegexList)
        }
        if (pathFilter(filterRegex, path)) {
            var fis: InputStream? = null
            var toClient: OutputStream? = null
            try {
                val file = File(filePath)
                if (!file.exists()) {
                    throw ServerException("the file [$filePath] is not exists")
                }
                val filename = file.name
                fis = BufferedInputStream(FileInputStream(file))
                val buffer = ByteArray(fis.available())
                if (fis.read(buffer) == -1) {
                    log.error("file：$filename is empty")
                }
                fis.close()
                response.reset()
                response.contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE
                response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, request.characterEncoding))
                response.setContentLength(file.length().toInt())
                toClient = BufferedOutputStream(response.outputStream)
                toClient.write(buffer)
                toClient.flush()
                toClient.close()
                log.debug("download file Success:$filename")
                if (isDelete) {
                    CommonTools.doDeleteFile(file, true)
                }
            } catch (e: Exception) {
                if (fis != null) {
                    try {
                        fis.close()
                    } catch (ex: IOException) {
                        log.error(ex.message, ex)
                    }

                }
                if (toClient != null) {
                    try {
                        toClient.close()
                    } catch (ex: IOException) {
                        log.error(ex.message, ex)
                    }

                }
                log.error(e.message, e)
                throw ServerException(e.message)
            }

        } else {
            throw ServerException("download file failed,the file path is not correct")
        }
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
