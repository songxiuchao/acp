package pers.acp.springboot.core.base;

import pers.acp.springboot.core.handle.HttpServletRequestAcp;
import pers.acp.springboot.core.handle.HttpServletResponseAcp;
import pers.acp.springboot.core.tools.ServletTools;
import pers.acp.core.CommonTools;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author zhangbin by 2018-1-21 1:55
 * @since JDK 11
 */
public abstract class BaseFilter implements Filter {

    private static String encode;

    @Override
    public void init(FilterConfig filterConfig) {
        encode = CommonTools.getDefaultCharset();
        if (CommonTools.isNullStr(encode)) {
            String charset = filterConfig.getInitParameter("encode");
            if (!CommonTools.isNullStr(charset)) {
                encode = charset;
            }
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletRequestAcp aRequest = ServletTools.parseRequest(req, encode);
        HttpServletResponseAcp aResponse = ServletTools.parseResponse(req, resp, encode);
        aResponse.setCharacterEncoding(encode);
        doFilter(aRequest, aResponse, chain);
    }

    public abstract void doFilter(HttpServletRequestAcp request, HttpServletResponseAcp response, FilterChain chain) throws IOException, ServletException;

}
