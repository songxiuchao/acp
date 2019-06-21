package pers.acp.spring.cloud.server.helloworld;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.support.FeignHttpClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import pers.acp.client.exceptions.HttpException;
import pers.acp.client.http.HttpClientBuilder;
import pers.acp.spring.cloud.annotation.AcpCloudAtomApplication;

/**
 * @author zhangbin by 2018-3-5 13:56
 * @since JDK 11
 */
@AcpCloudAtomApplication
public class HelloWorldApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloWorldApplication.class, args);
    }

    @Bean("customerRestTemplateTest")
    @LoadBalanced
    public RestTemplate restTemplate(FeignHttpClientProperties feignHttpClientProperties) throws HttpException {
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory(
                new HttpClientBuilder().maxTotalConn(feignHttpClientProperties.getMaxConnections())
                        .maxPerRoute(feignHttpClientProperties.getMaxConnectionsPerRoute())
                        .timeOut(feignHttpClientProperties.getConnectionTimeout()).build().getHttpClient()));
    }

}
