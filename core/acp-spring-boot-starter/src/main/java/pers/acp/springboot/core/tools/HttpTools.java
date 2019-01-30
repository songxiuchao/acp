package pers.acp.springboot.core.tools;

import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.regex.Pattern;

public final class HttpTools {

    private static final LogFactory log = LogFactory.getInstance(HttpTools.class);// 日志对象

    /**
     * 通过request获取项目webroot路径
     *
     * @return 项目webroot路径
     */
    public static String getWebRootPath(HttpServletRequest request) {
        String webroot = request.getContextPath();
        if (webroot.equals("/")) {
            return "";
        } else {
            return webroot;
        }
    }

    /**
     * 获取客户端发送的内容（xml或json）字符串
     *
     * @param request 请求对象
     * @return 请求内容
     */
    public static String getRequestContent(HttpServletRequest request) {
        ServletInputStream sis = null;
        try {
            sis = request.getInputStream();
            int size = request.getContentLength();
            if (size <= 0) {
                return "";
            }
            byte[] buffer = new byte[size];
            byte[] dataByte = new byte[size];
            int count = 0;
            int rbyte;
            while (count < size) {
                rbyte = sis.read(buffer);
                if (rbyte > 0) {
                    System.arraycopy(buffer, 0, dataByte, count, rbyte);
                    count += rbyte;
                } else {
                    break;
                }
            }
            if (count <= 0) {
                return "";
            }
            return new String(dataByte, request.getCharacterEncoding());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return "";
        } finally {
            if (sis != null) {
                try {
                    sis.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * uri是否被识别
     *
     * @param uri   请求url字符串
     * @param regex 正则表达式
     * @return true|false
     */
    public static boolean isBeIdentifiedUri(String uri, String regex) {
        return CommonTools.regexPattern(regex, uri);
    }

}
