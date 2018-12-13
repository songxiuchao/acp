package pers.acp.springboot.core.filter;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import pers.acp.springboot.core.base.BaseFilter;
import pers.acp.springboot.core.handle.HttpServletRequestAcp;
import pers.acp.springboot.core.handle.HttpServletResponseAcp;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import java.io.IOException;

/**
 * Created by zhangbin on 2017-6-16.
 * 请求过滤拦截
 */
@WebFilter(urlPatterns = "/*", filterName = "AcpRequestFilter", initParams = {@WebInitParam(name = "encode", value = "utf-8")})
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AcpRequestFilter extends BaseFilter {

    @Override
    public void doFilter(HttpServletRequestAcp request, HttpServletResponseAcp response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
