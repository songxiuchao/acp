package pers.acp.springboot.core.base;

import pers.acp.springboot.core.handle.HttpServletRequestAcp;
import pers.acp.springboot.core.handle.HttpServletResponseAcp;
import pers.acp.core.CommonTools;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zhangbin by 2018-1-21 1:52
 * @since JDK1.8
 */
public abstract class BaseServlet extends HttpServlet {

    private static final long serialVersionUID = -217150319870839022L;

    protected String encode;

    public BaseServlet() {
        encode = CommonTools.getDefaultCharset();
        if (CommonTools.isNullStr(encode)) {
            ServletConfig config = getServletConfig();
            String charset = config.getInitParameter("encode");
            if (!CommonTools.isNullStr(charset)) {
                encode = charset;
            }
        }
    }

    public void service(HttpServletRequest req, HttpServletResponse resp) {
        service((HttpServletRequestAcp) req, (HttpServletResponseAcp) resp);
    }

    public abstract void service(HttpServletRequestAcp aRequest, HttpServletResponseAcp aResponse);

}
