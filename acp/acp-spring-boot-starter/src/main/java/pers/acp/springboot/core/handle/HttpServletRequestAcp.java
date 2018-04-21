package pers.acp.springboot.core.handle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class HttpServletRequestAcp extends HttpServletRequestWrapper {

    private String oldCharset;

    public HttpServletRequestAcp(String oldCharset, HttpServletRequest request) {
        super(request);
        this.oldCharset = oldCharset;
    }

    /**
     * 获取客户端请求时使用的字符集，默认为utf-8
     *
     * @return 字符集
     */
    public String getOldCharset() {
        return this.oldCharset;
    }

}
