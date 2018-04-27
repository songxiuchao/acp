package pers.acp.springboot.core.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;
import pers.acp.springboot.core.conf.ControllerAspectConfiguration;
import pers.acp.springboot.core.tools.ServletTools;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Create by zhangbin on 2017-11-21 10:06
 * controller拦截器
 */
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ControllerAspect {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    private final ControllerAspectConfiguration controllerAspectConfiguration;

    @Autowired
    public ControllerAspect(ControllerAspectConfiguration controllerAspectConfiguration) {
        this.controllerAspectConfiguration = controllerAspectConfiguration;
    }

    /**
     * 定义拦截规则
     */
    @Pointcut(value = "execution(public * *(..)) && (" +
            "@within(org.springframework.web.bind.annotation.RestController) " +
            "|| @within(org.springframework.stereotype.Controller)) && ( " +
            "@annotation(org.springframework.web.bind.annotation.RequestMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.GetMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.PostMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.PatchMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.DeleteMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.PutMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.Mapping) ) ")
    public void excudeService() {
    }

    /**
     * 拦截器具体实现
     *
     * @param pjp 拦截对象
     * @return Object（被拦截方法的执行结果）
     */
    @Around("excudeService()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        Object response = null;
        long beginTime = System.currentTimeMillis();
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        if (sra != null) {
            HttpServletRequest request = sra.getRequest();
            String method = request.getMethod();
            String uri = request.getRequestURI();
            if (needLog(uri)) {
                StringBuilder startLog = new StringBuilder("========== 请求开始, method: {}, Content-Type: {}, uri: {}\n");
                startLog.append("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
                startLog.append("target : ").append(pjp.getSignature().getDeclaringTypeName()).append("\n");
                startLog.append("-----> request: ").append(method).append("\n");
                Enumeration<String> headers = request.getHeaderNames();
                if (headers.hasMoreElements()) {
                    startLog.append("      ┖---- headers: \n");
                }
                while (headers.hasMoreElements()) {
                    String name = headers.nextElement();
                    startLog.append("           - ").append(name).append("=").append(request.getHeader(name)).append("\n");
                }
                String queryString = request.getQueryString();
                if (!CommonTools.isNullStr(queryString)) {
                    startLog.append("      ┖---- query string: \n").append("           - ").append(queryString).append("\n");
                }
                Enumeration<String> params = request.getParameterNames();
                if (params.hasMoreElements()) {
                    startLog.append("      ┖---- parameter: \n");
                }
                while (params.hasMoreElements()) {
                    String name = params.nextElement();
                    startLog.append("           - ").append(name).append("=").append(request.getParameter(name)).append("\n");
                }
                startLog.append("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
                log.info(startLog.toString(), method, request.getContentType(), uri);
                log.info(">>>>>>>>>> 请求处理开始...  [method: {}, uri: {}]", method, uri);
            }
            long processBegin = System.currentTimeMillis();
            response = pjp.proceed();
            if (needLog(uri)) {
                log.info(">>>>>>>>>> 请求处理结束! [method: {}, uri: {}, 处理耗时: {} 毫秒]", method, uri, System.currentTimeMillis() - processBegin);
                StringBuilder endLog = new StringBuilder("\n----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
                endLog.append("-----> response: ").append(response.toString()).append("\n");
                String responseInfo = null;
                if (response instanceof ResponseEntity) {
                    ResponseEntity responseEntity = ((ResponseEntity) response);
                    if (responseEntity.getBody() != null) {
                        responseInfo = responseEntity.getBody().toString();
                    }
                } else {
                    responseInfo = CommonTools.objectToJson(response).toString();
                }
                endLog.append("      ┖---- body: \n").append(responseInfo).append("\n");
                endLog.append("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
                log.info(endLog.toString());
                log.info("========== 请求结束! [method: {}, uri: {}, 总耗时: {} 毫秒]", method, uri, (System.currentTimeMillis() - beginTime));
            }
        }
        return response;
    }

    /**
     * 匹配uri
     *
     * @param uri uri
     * @return true|false
     */
    private boolean needLog(String uri) {
        List<String> noLogUriRegexes = controllerAspectConfiguration.getNoLogUriRegexes();
        if (noLogUriRegexes != null) {
            for (String regex : noLogUriRegexes) {
                if (ServletTools.isBeIdentifiedUri(uri, regex)) {
                    return false;
                }
            }
        }
        return true;
    }

}
