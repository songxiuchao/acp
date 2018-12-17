package pers.acp.packet.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;
import pers.acp.packet.xml.XmlPacket;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author zhangbin by 2018-2-1 22:20
 * @since JDK 11
 */
public class HttpPacket {

    /**
     * 日志对象
     */
    private static final LogFactory log = LogFactory.getInstance(HttpPacket.class);

    /**
     * url编码
     *
     * @param url      url字符串
     * @param encoding 字符编码
     * @return 结果字符串
     */
    public static String urlEncoding(String url, String encoding) {
        try {
            return URLEncoder.encode(url, encoding);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

    /**
     * url解码
     *
     * @param url      url字符串
     * @param encoding 字符编码
     * @return 结果字符串
     */
    public static String urlDecoding(String url, String encoding) {
        try {
            return URLDecoder.decode(url, encoding);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

    /**
     * 构建GET请求参数
     *
     * @param url           url字符串
     * @param params        参数
     * @param clientCharset 字符集
     * @return url字符串
     */
    public static String buildGetParam(String url, Map<String, String> params, String clientCharset) {
        try {
            if (params != null) {
                StringBuilder urlBuilder = new StringBuilder(url);
                for (Map.Entry<String, String> stringStringEntry : params.entrySet()) {
                    String sepStr;
                    if (!urlBuilder.toString().contains("?")) {
                        sepStr = "?";
                    } else {
                        sepStr = "&";
                    }
                    String key = stringStringEntry.getKey();
                    String val = stringStringEntry.getValue();
                    urlBuilder.append(sepStr).append(key).append("=").append(URLEncoder.encode(val, clientCharset));
                }
                url = urlBuilder.toString();
            }
            return url;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

    /**
     * 构建POST请求XML
     *
     * @param params        参数
     * @param rootName      null则使用默认“xml”
     * @param clientCharset 字符集
     * @return xml字符串
     */
    public static String buildPostXMLParam(Map<String, String> params, String rootName, String clientCharset) {
        return buildPostXMLParam(params, rootName, clientCharset, false);
    }

    /**
     * 构建POST请求XML
     *
     * @param params        参数
     * @param rootName      null则使用默认“xml”
     * @param clientCharset 字符集
     * @param isIndent      是否自动格式化
     * @return xml字符串
     */
    public static String buildPostXMLParam(Map<String, String> params, String rootName, String clientCharset, boolean isIndent) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        String root = "xml";
        if (!CommonTools.isNullStr(rootName)) {
            root = rootName;
        }
        ObjectNode info = mapper.createObjectNode();
        ArrayNode children = mapper.createArrayNode();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            ObjectNode einfo = mapper.createObjectNode();
            einfo.put("value", value);
            einfo.put("isCDATA", true);
            ObjectNode element = mapper.createObjectNode();
            element.set(key, einfo);
            children.add(element);
        }
        info.set("children", children);
        json.set(root, info);
        return XmlPacket.jsonToXML(json, clientCharset, isIndent);
    }

}
