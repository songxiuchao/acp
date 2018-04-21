package pers.acp.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import pers.acp.client.exceptions.HttpException;
import pers.acp.client.http.AcpClient;
import pers.acp.client.http.HttpClientBuilder;
import pers.acp.springcloud.common.annotation.AcpCloudAtomApplication;

/**
 * @author zhangbin by 21/03/2018 10:10
 * @since JDK1.8
 */
@AcpCloudAtomApplication
public class SpringCloudTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudTestApplication.class, args);
    }

    /**
     * 创建 RestTemplate 客户端 bean
     * 在其他地方注入时，如果需要负载均衡，则加上@LoadBalanced即可
     */
    @LoadBalanced
    @Bean
    RestTemplate restTemplate() throws HttpException {
        AcpClient acpClient = new HttpClientBuilder().build();
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(acpClient.getHttpClient());
        return new RestTemplate(clientHttpRequestFactory);
    }

}
