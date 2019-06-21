package pers.acp.springcloud.server.helloworld.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import pers.acp.springboot.vo.ErrorVO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author zhang by 05/03/2019
 * @since JDK 11
 */
@Component("AuthExceptionEntryPoint")
public class AuthExceptionEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Autowired
    public AuthExceptionEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ErrorVO errorVO = new ErrorVO();
        Throwable cause = authException.getCause();
        if (cause instanceof InvalidTokenException) {
            errorVO.setError("无效的 token ");
            errorVO.setErrorDescription("无效的 token ");
        } else {
            errorVO.setError("访问此资源需要完全的身份验证");
            errorVO.setErrorDescription("访问此资源需要完全的身份验证");
        }
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        objectMapper.writeValue(response.getOutputStream(), errorVO);
    }
}
