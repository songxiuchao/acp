package pers.acp.springcloud.common.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.OAuth2ClientProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.web.client.RestTemplate;
import pers.acp.client.exceptions.HttpException;
import pers.acp.client.http.HttpClientBuilder;
import pers.acp.core.log.LogFactory;

/**
 * @author zhangbin by 11/04/2018 15:13
 * @since JDK1.8
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    private final OAuth2ClientProperties clientProperties;

    private final ResourceServerProperties resourceServerProperties;

    @Autowired
    public ResourceServerConfiguration(OAuth2ClientProperties clientProperties, ResourceServerProperties resourceServerProperties) {
        this.clientProperties = clientProperties;
        this.resourceServerProperties = resourceServerProperties;
    }

    @Bean
    public RemoteTokenServices remoteTokenServices() {
        RemoteTokenServices services = new RemoteTokenServices();
        try {
            services.setRestTemplate(new RestTemplate(new HttpComponentsClientHttpRequestFactory(new HttpClientBuilder().build().getHttpClient())));
        } catch (HttpException e) {
            log.error(e.getMessage(), e);
        }
        services.setCheckTokenEndpointUrl(resourceServerProperties.getTokenInfoUri());
        services.setClientId(clientProperties.getClientId());
        services.setClientSecret(clientProperties.getClientSecret());
        return services;
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.tokenServices(remoteTokenServices());
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests().antMatchers(
                "/error",
                "/download",
                "/acperror",
                "/actuator",
                "/actuator/**",
                "/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**",
                "/swagger-resources/configuration/ui",
                "**.stream").permitAll();
    }

}
