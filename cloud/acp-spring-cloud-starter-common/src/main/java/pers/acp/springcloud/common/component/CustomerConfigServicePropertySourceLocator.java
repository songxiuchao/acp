package pers.acp.springcloud.common.component;

import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.client.ConfigServicePropertySourceLocator;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import pers.acp.springboot.core.tools.PackageTools;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.cloud.config.client.ConfigClientProperties.AUTHORIZATION;

/**
 * @author zhang by 28/02/2019
 * @since JDK 11
 */
public class CustomerConfigServicePropertySourceLocator extends ConfigServicePropertySourceLocator {

    private final ConfigurableEnvironment environment;

    public CustomerConfigServicePropertySourceLocator(ConfigurableEnvironment environment, ConfigClientProperties clientProperties) {
        super(clientProperties);
        this.environment = environment;
        setRestTemplate(getSecureRestTemplate(clientProperties));
    }

    private RestTemplate getSecureRestTemplate(ConfigClientProperties client) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        if (client.getRequestReadTimeout() < 0) {
            throw new IllegalStateException("Invalid Value for Read Timeout set.");
        }
        requestFactory.setReadTimeout(client.getRequestReadTimeout());
        RestTemplate template = new RestTemplate(requestFactory);
        for (HttpMessageConverter httpMessageConverter : template.getMessageConverters()) {
            if (httpMessageConverter instanceof MappingJackson2HttpMessageConverter) {
                template.getMessageConverters().remove(httpMessageConverter);
                template.getMessageConverters().add(new MappingJackson2HttpMessageConverter(PackageTools.buildJacksonObjectMapper(environment)));
                break;
            }
        }
        Map<String, String> headers = new HashMap<>(client.getHeaders());
        headers.remove(AUTHORIZATION);
        if (!headers.isEmpty()) {
            template.setInterceptors(Collections.singletonList(
                    new ConfigServicePropertySourceLocator.GenericRequestHeaderInterceptor(headers)));
        }
        return template;
    }

}
