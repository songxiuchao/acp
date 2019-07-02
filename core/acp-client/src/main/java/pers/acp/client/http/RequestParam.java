package pers.acp.client.http;

import okhttp3.RequestBody;
import pers.acp.core.CommonTools;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhang by 01/07/2019
 * @since JDK 11
 */
public class RequestParam {

    RequestParam() {
    }

    private String url = null;

    private String clientCharset = CommonTools.getDefaultCharset();

    private String basicUsername = null;

    private String basicPassword = null;

    private Map<String, String> requestHeaders = new HashMap<>();

    private Map<String, String> params = new HashMap<>();

    private String bodyString = "";

    private byte[] bodyBytes;

    private RequestBody body;

    public void addRequestHeader(String name, String value) {
        this.requestHeaders.put(name, value);
    }

    public RequestParam url(String url) {
        this.url = url;
        return this;
    }

    public RequestParam clientCharset(String clientCharset) {
        this.clientCharset = clientCharset;
        return this;
    }

    public RequestParam basicUsername(String basicUsername) {
        this.basicUsername = basicUsername;
        return this;
    }

    public RequestParam basicPassword(String basicPassword) {
        this.basicPassword = basicPassword;
        return this;
    }

    public RequestParam body(String body) {
        this.bodyString = body;
        return this;
    }

    public RequestParam body(byte[] body) {
        this.bodyBytes = body;
        return this;
    }

    public RequestParam body(RequestBody body) {
        this.body = body;
        return this;
    }

    public RequestParam params(Map<String, String> params) {
        this.params = params;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public String getClientCharset() {
        return clientCharset;
    }

    public String getBasicUsername() {
        return basicUsername;
    }

    public String getBasicPassword() {
        return basicPassword;
    }

    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public String getBodyString() {
        return bodyString;
    }

    public byte[] getBodyBytes() {
        return bodyBytes;
    }

    public RequestBody getBody() {
        return body;
    }
}
