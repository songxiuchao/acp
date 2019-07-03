import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import pers.acp.client.exceptions.HttpException;
import pers.acp.client.http.HttpClientBuilder;
import pers.acp.client.http.RequestParamBuilder;
import pers.acp.client.http.ResponseResult;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhang by 03/07/2019
 * @since JDK 11
 */
class TestHttp {

    @Test
    void doTest() {
        try {
            doPost();
            doPostString();
            doPostBytes();
            doGet();
            doGetHttps();
        } catch (HttpException e) {
            e.printStackTrace();
        }
    }

    @Test
    void doPost() throws HttpException {
        Map<String, String> map = new HashMap<>();
        map.put("grant_type", "client_credentials");
        map.put("client_id", "test");
        map.put("client_secret", "test");
        long begin = System.currentTimeMillis();
        ResponseResult responseResult = new HttpClientBuilder().build()
                .doPost(RequestParamBuilder.build()
                        .url("http://127.0.0.1:9090/boot/mapform")
                        .params(map));
        System.out.println("doPost -----> " + responseResult);
        System.out.println("doPost -----> " + responseResult.getStatus());
        System.out.println("doPost -----> " + responseResult.getBody());
        System.out.println("doPost -----> " + (System.currentTimeMillis() - begin));
    }

    @Test
    void doPostString() throws HttpException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode body = mapper.createObjectNode();
        body.put("param1", "尼玛");
        body.put("param2", "3");
        long begin = System.currentTimeMillis();
        ResponseResult responseResult = new HttpClientBuilder().build()
                .doPostJson(RequestParamBuilder.build()
                        .url("http://127.0.0.1:9090/boot/map")
                        .body(body.toString()));
        System.out.println("doPostString -----> " + responseResult);
        System.out.println("doPostString -----> " + responseResult.getStatus());
        System.out.println("doPostString -----> " + responseResult.getBody());
        System.out.println("doPostString -----> " + (System.currentTimeMillis() - begin));
    }

    @Test
    void doPostBytes() throws HttpException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode body = mapper.createObjectNode();
        body.put("param1", "尼玛");
        body.put("param2", "3");
        long begin = System.currentTimeMillis();
        ResponseResult responseResult = new HttpClientBuilder().build()
                .doPostBytes(RequestParamBuilder.build()
                        .url("http://127.0.0.1:9090/boot/map")
                        .body(body.toString().getBytes()));
        System.out.println("doPostBytes -----> " + responseResult);
        System.out.println("doPostBytes -----> " + responseResult.getStatus());
        System.out.println("doPostBytes -----> " + responseResult.getBody());
        System.out.println("doPostBytes -----> " + (System.currentTimeMillis() - begin));
    }

    @Test
    void doGet() throws HttpException {
        Map<String, String> map = new HashMap<>();
        map.put("pwd", "password");
        long begin = System.currentTimeMillis();
        ResponseResult responseResult = new HttpClientBuilder().build()
                .doGet(RequestParamBuilder.build()
                        .url("http://127.0.0.1:9090/boot/rest1/testget")
                        .params(map));
        System.out.println("doGet -----> " + responseResult);
        System.out.println("doGet -----> " + responseResult.getStatus());
        System.out.println("doGet -----> " + responseResult.getBody());
        System.out.println("doGet -----> " + (System.currentTimeMillis() - begin));
    }

    @Test
    void doGetHttps() throws HttpException {
        long begin = System.currentTimeMillis();
        ResponseResult responseResult = new HttpClientBuilder()
                .disableSslValidation(true)
//                .sslProtocolVersion("TLSv1.2")
                .build()
                .doGet(RequestParamBuilder.build()
                        .url("https://github.com/zhangbin1010/acp-admin"));
        System.out.println("doGetHttps -----> " + responseResult);
        System.out.println("doGetHttps -----> " + responseResult.getStatus());
        System.out.println("doGetHttps -----> " + responseResult.getBody());
        System.out.println("doGetHttps -----> " + (System.currentTimeMillis() - begin));
    }

}
