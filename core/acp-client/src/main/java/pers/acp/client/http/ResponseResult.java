package pers.acp.client.http;

import okhttp3.Headers;
import okhttp3.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangbin by 2018-2-22 23:10
 * @since JDK 11
 */
public class ResponseResult {

    ResponseResult() {
    }

    public int getStatus() {
        return status;
    }

    public Map<String, List<String>> getHeaders() {
        return responseHeaders;
    }

    public String getBody() {
        return body;
    }

    public Response getResponse() {
        return response;
    }

    public ResponseResult status(int status) {
        this.status = status;
        return this;
    }

    public ResponseResult headers(Headers headers) {
        this.responseHeaders = headers.toMultimap();
        return this;
    }

    public ResponseResult body(String body) {
        this.body = body;
        return this;
    }

    public ResponseResult response(Response response) {
        this.response = response;
        return this;
    }

    @Override
    public String toString() {
        return "status=" + status + ",body=" + body;
    }

    private int status;

    private Map<String, List<String>> responseHeaders = new HashMap<>();

    private String body;

    private Response response;

}
