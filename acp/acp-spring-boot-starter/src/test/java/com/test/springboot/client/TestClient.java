package com.test.springboot.client;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import pers.acp.client.exceptions.HttpException;
import pers.acp.client.http.AcpClient;
import pers.acp.client.http.HttpClientBuilder;

/**
 * @author zhangbin by 18/03/2018 04:00
 * @since JDK1.8
 */
public class TestClient {

    public static void main(String[] args) throws HttpException {
        AcpClient acpClient = new HttpClientBuilder().build();
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(acpClient.getHttpClient());
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
        String result = restTemplate.getForObject("http://www.baidu.com", String.class);
        System.out.println(result);
    }

}
