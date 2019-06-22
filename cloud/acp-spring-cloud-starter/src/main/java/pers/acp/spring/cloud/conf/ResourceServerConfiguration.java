package pers.acp.spring.cloud.conf;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.oauth2.OAuth2ClientProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.support.FeignHttpClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import pers.acp.client.exceptions.HttpException;
import pers.acp.client.http.HttpClientBuilder;
import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;
import pers.acp.spring.cloud.constant.ConfigurationOrder;
import pers.acp.spring.cloud.enums.RestPrefix;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Oauth2 资源服务配置
 *
 * @author zhangbin by 11/04/2018 15:13
 * @since JDK 11
 */
@Configuration
@EnableResourceServer
@Order(ConfigurationOrder.resourceServerConfiguration)
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    private final AcpOauthConfiguration acpOauthConfiguration;

    private final Map<String, AuthenticationEntryPoint> entryPointMap;

    private final Map<String, AccessDeniedHandler> accessDeniedHandlerMap;

    private final OAuth2ClientProperties clientProperties;

    private final ResourceServerProperties resourceServerProperties;

    private final FeignHttpClientProperties feignHttpClientProperties;

    private final ObjectMapper objectMapper;

    private final String contextPath;

    @Autowired
    public ResourceServerConfiguration(AcpOauthConfiguration acpOauthConfiguration, Map<String, AuthenticationEntryPoint> entryPointMap, Map<String, AccessDeniedHandler> accessDeniedHandlerMap, OAuth2ClientProperties clientProperties, ResourceServerProperties resourceServerProperties, FeignHttpClientProperties feignHttpClientProperties, ObjectMapper objectMapper, ServerProperties serverProperties) {
        this.acpOauthConfiguration = acpOauthConfiguration;
        this.entryPointMap = entryPointMap;
        this.accessDeniedHandlerMap = accessDeniedHandlerMap;
        this.clientProperties = clientProperties;
        this.resourceServerProperties = resourceServerProperties;
        this.feignHttpClientProperties = feignHttpClientProperties;
        this.objectMapper = objectMapper;
        this.contextPath = CommonTools.isNullStr(serverProperties.getServlet().getContextPath()) ? "" : serverProperties.getServlet().getContextPath();
    }

    @LoadBalanced
    @Bean("acpSpringCloudOauth2ClientRestTemplate")
    public RestTemplate acpSpringCloudOauth2ClientRestTemplate() throws HttpException {
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(
                new HttpClientBuilder().maxTotalConn(feignHttpClientProperties.getMaxConnections())
                        .maxPerRoute(feignHttpClientProperties.getMaxConnectionsPerRoute())
                        .timeOut(feignHttpClientProperties.getConnectionTimeout()).build().getHttpClient()));
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter(objectMapper));
        return restTemplate;
    }

    @Bean
    @ConditionalOnMissingBean
    public ServerProperties serverProperties() {
        return new ServerProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public ResourceServerProperties resourceServerProperties() {
        return new ResourceServerProperties();
    }

    /**
     * 自定义权限验证服务，远程调用认证服务进行验证
     *
     * @return 远程 token 认证服务实例
     */
    @Primary
    @Bean("acpResourceServerRemoteTokenServices")
    @ConditionalOnExpression("!'${acp.cloud.oauth.oauth-server}'.equals('true')")
    public RemoteTokenServices remoteTokenServices() {
        RemoteTokenServices services = new RemoteTokenServices();
        // 自定义错误处理类，所有错误放行统一交由 oauth 模块进行进出
        try {
            RestTemplate restTemplate = acpSpringCloudOauth2ClientRestTemplate();
            restTemplate.setErrorHandler(new ResponseErrorHandler() {
                @Override
                public boolean hasError(ClientHttpResponse response) {
                    return false;
                }

                @Override
                public void handleError(ClientHttpResponse response) {

                }
            });
            services.setRestTemplate(restTemplate);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        services.setCheckTokenEndpointUrl(resourceServerProperties.getTokenInfoUri());
        services.setClientId(clientProperties.getClientId());
        services.setClientSecret(clientProperties.getClientSecret());
        return services;
    }

    /**
     * 设置 token 验证服务
     *
     * @param resources 资源服务安全验证配置对象
     */
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        if (!acpOauthConfiguration.isOauthServer()) {
            resources.tokenServices(remoteTokenServices());
        }
        // 自定义 token 异常处理
        if (!entryPointMap.isEmpty()) {
            if (entryPointMap.size() > 1) {
                if (!CommonTools.isNullStr(acpOauthConfiguration.getAuthExceptionEntryPoint())) {
                    resources.authenticationEntryPoint(entryPointMap.get(acpOauthConfiguration.getAuthExceptionEntryPoint()));
                } else {
                    log.warn("Find more than one authenticationEntryPoint, please specify explicitly in the configuration 'acp.cloud.auth.auth-exception-entry-point'");
                }
            } else {
                resources.authenticationEntryPoint(entryPointMap.entrySet().iterator().next().getValue());
            }
        }
        // 自定义权限异常处理
        if (!accessDeniedHandlerMap.isEmpty()) {
            if (accessDeniedHandlerMap.size() > 1) {
                if (!CommonTools.isNullStr(acpOauthConfiguration.getAccessDeniedHandler())) {
                    resources.accessDeniedHandler(accessDeniedHandlerMap.get(acpOauthConfiguration.getAccessDeniedHandler()));
                } else {
                    log.warn("Find more than one accessDeniedHandler, please specify explicitly in the configuration 'acp.cloud.auth.access-denied-handler'");
                }
            } else {
                resources.accessDeniedHandler(accessDeniedHandlerMap.entrySet().iterator().next().getValue());
            }
        }
    }

    /**
     * http 验证策略配置
     *
     * @param http http 安全验证对象
     * @throws Exception 异常
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        List<String> permitAll = new ArrayList<>();
        List<String> security = new ArrayList<>();
        if (acpOauthConfiguration.isResourceServer()) {
            log.info("resource server = true");
            permitAll.add(contextPath + "/error");
            permitAll.add(contextPath + "/actuator");
            permitAll.add(contextPath + "/actuator/**");
            permitAll.add(contextPath + "/v2/api-docs");
            permitAll.add(contextPath + "/configuration/ui");
            permitAll.add(contextPath + "/swagger-resources/**");
            permitAll.add(contextPath + "/configuration/security");
            permitAll.add(contextPath + "/swagger-ui.html");
            permitAll.add(contextPath + "/webjars/**");
            permitAll.add(contextPath + "/swagger-resources/configuration/ui");
            permitAll.add(contextPath + "/hystrix.stream");
            permitAll.add(contextPath + "/oauth/authorize");
            permitAll.add(contextPath + "/oauth/token");
            permitAll.add(contextPath + "/oauth/error");
            acpOauthConfiguration.getResourceServerPermitAllPath().forEach(path -> permitAll.add(contextPath + path));
            acpOauthConfiguration.getResourceServerSecurityPath().forEach(path -> security.add(contextPath + path));
            permitAll.add(contextPath + RestPrefix.OPEN + "/**");
        } else {
            log.info("resource server = false");
            permitAll.add(contextPath + "/**");
        }
        permitAll.forEach(uri -> log.info("permitAll uri: " + uri));
        security.forEach(uri -> log.info("security uri: " + uri));
        log.info("security uri: other any");
        // match 匹配的url，赋予全部权限（不进行拦截）
        http.csrf().disable().authorizeRequests()
                .antMatchers(security.toArray(new String[]{})).authenticated()
                .antMatchers(permitAll.toArray(new String[]{})).permitAll()
                .anyRequest().authenticated()
                .and().formLogin().permitAll();
    }

}
