package pers.acp.client.http;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

/**
 * @author zhangbin by 2018-2-22 23:10
 * @since JDK1.8
 */
public class ResponseResult {

    public int getStatus() {
        return status;
    }

    public Header[] getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public HttpResponse getResponse() {
        return response;
    }

    public ResponseResult status(int status) {
        this.status = status;
        return this;
    }

    public ResponseResult headers(Header[] headers) {
        this.headers = headers;
        return this;
    }

    public ResponseResult body(String body) {
        this.body = body;
        return this;
    }

    public ResponseResult response(HttpResponse response) {
        this.response = response;
        return this;
    }

    @Override
    public String toString() {
        return "status=" + status + ",body=" + body;
    }

    private int status;

    private Header[] headers;

    private String body;

    private HttpResponse response;

}
