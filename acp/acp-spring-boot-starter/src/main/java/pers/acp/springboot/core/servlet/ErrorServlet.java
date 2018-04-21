package pers.acp.springboot.core.servlet;

import pers.acp.springboot.core.base.BaseServlet;
import pers.acp.springboot.core.handle.HttpServletRequestAcp;
import pers.acp.springboot.core.handle.HttpServletResponseAcp;
import pers.acp.springboot.core.variable.ResponseKeys;
import pers.acp.core.CommonTools;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

/**
 * Created by zhangbin on 2017-6-16.
 * 返回错误信息的servlet
 */
@WebServlet(name = "ErrorServlet", urlPatterns = "/acperror", initParams = {@WebInitParam(name = "encode", value = "utf-8")})
public class ErrorServlet extends BaseServlet {

    private static final long serialVersionUID = -618667313352831966L;

    @Override
    public void service(HttpServletRequestAcp aRequest, HttpServletResponseAcp aResponse) {
        String errmsg = aRequest.getParameter(ResponseKeys.responseErrorMsgKey);
        if (CommonTools.isNullStr(errmsg)) {
            errmsg = "请求非法";
        }
        aResponse.doReturnError(errmsg);
    }

}
