package pers.acp.springcloud.common.component;

import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.client.ConfigServicePropertySourceLocator;
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
 * @author zhang by 28/02/2019
 * @since JDK 11
 */
public class CustomerConfigServicePropertySourceLocator extends ConfigServicePropertySourceLocator {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    private final ConfigurableEnvironment environment;

    public CustomerConfigServicePropertySourceLocator(ConfigurableEnvironment environment, ConfigClientProperties clientProperties) {
        super(clientProperties);
        this.environment = environment;
        setRestTemplate(customerConfigClientRestTemplate(clientProperties));
        log.info("Start Up Cloud, Configuration CustomerConfigServicePropertySourceLocator For ACP");
    }

    private RestTemplate customerConfigClientRestTemplate(ConfigClientProperties clientProperties) {
        try {
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(new HttpClientBuilder().build().getHttpClient());
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