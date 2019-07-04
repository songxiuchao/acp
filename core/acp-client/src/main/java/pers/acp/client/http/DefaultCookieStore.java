package pers.acp.client.http;

import okhttp3.Cookie;
import okhttp3.HttpUrl;
import pers.acp.client.http.base.CookieStore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhang by 01/07/2019
 * @since JDK 11
 */
public class DefaultCookieStore implements CookieStore {

    private final ConcurrentHashMap<String, List<Cookie>> concurrentHashMap = new ConcurrentHashMap<>();

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        concurrentHashMap.put(url.host(), cookies);
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> cookies = concurrentHashMap.get(url.host());
        return cookies != null ? cookies : new ArrayList<>();
    }

}
