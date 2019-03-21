package pers.acp.springcloud.common.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.client.ConfigServicePropertySourceLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import pers.acp.client.http.HttpClientBuilder;
import pers.acp.core.log.LogFactory;
import pers.acp.springboot.core.tools.PackageTools;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.cloud.config.client.ConfigClientProperties.AUTHORIZATION;

/**
 * @author zhang by 21/03/2019
 * @since JDK 11
 */
@Configuration
@EnableConfigurationProperties
public class AcpConfigServerBootstrapConfiguration {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    private final ConfigurableEnvironment environment;

    @Autowired
    public AcpConfigServerBootstrapConfiguration(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    @Primary
    @Bean
    @ConditionalOnProperty(value = "spring.cloud.config.enabled", matchIfMissing = true)
    public ConfigServicePropertySourceLocator configServicePropertySource(ConfigClientProperties clientProperties) {
        ConfigServicePropertySourceLocator locator = new ConfigServicePropertySourceLocator(clientProperties);
        locator.setRestTemplate(customerConfigClientRestTemplate(clientProperties));
        return locator;
    }

    private RestTemplate customerConfigClientRestTemplate(ConfigClientProperties clientProperties) {
        try {
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(
                    new HttpClientBuilder().maxTotalConn(Integer.valueOf(environment.getProperty("feign.httpclient.max-connections", "1000")))
                            .maxPerRoute(Integer.valueOf(environment.getProperty("feign.httpclient.max-connections-per-route", "50")))
                            .timeOut(Integer.valueOf(environment.getProperty("feign.httpclient.connection-timeout", "10000")))
                            .build().getHttpClient());
            if (clientProperties.getRequestReadTimeout() < 0) {
                throw new IllegalStateException("Invalid Value for Read Timeout set.");
            }
            requestFactory.setReadTimeout(clientProperties.getRequestReadTimeout());
            RestTemplate template = new RestTemplate(requestFactory);
            for (HttpMessageConverter httpMessageConverter : template.getMessageConverters()) {
                if (httpMessageConverter instanceof MappingJackson2HttpMessageConverter) {
                    template.getMessageConverters().remove(httpMessageConverter);
                    template.getMessageConverters().add(new MappingJackson2HttpMessageConverter(PackageTools.buildJacksonObjectMapper(environment)));
                    break;
                }
            }
            Map<String, String> headers = new HashMap<>(clientProperties.getHeaders());
            headers.remove(AUTHORIZATION);
            if (!headers.isEmpty()) {
                template.setInterceptors(Collections.singletonList(
                        new ConfigServicePropertySourceLocator.GenericRequestHeaderInterceptor(headers)));
            }
            log.info("Created CustomerConfigClientRestTemplate For ACP");
            return template;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

}