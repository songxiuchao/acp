package pers.acp.springboot.core.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import pers.acp.core.CommonTools;
import pers.acp.springboot.core.enums.ResponseCode;
import pers.acp.springboot.core.variable.ResponseKeys;

/**
 * Created by Shepherd on 2016-08-05.
 * 报文工具类
 */
public class PackageTools {

    /**
     * 构建响应报文
     *
     * @param responseCode 响应代码
     * @param msg          响应信息
     * @param jsonNode     响应json对象
     * @return 响应报文JSON对象
     */
    public static ObjectNode buildResponsePackage(ResponseCode responseCode, String msg, JsonNode jsonNode) {
        if (CommonTools.isNullStr(msg)) {
            msg = responseCode.getName();
        }
        return buildResponsePackage(responseCode.getValue(), msg, jsonNode);
    }

    /**
     * 构建响应报文
     *
     * @param responseCode 响应代码
     * @param msg          响应信息
     * @param infoStr      响应内容
     * @return 响应报文JSON对象
     */
    public static ObjectNode buildResponsePackage(ResponseCode responseCode, String msg, String infoStr) {
        if (CommonTools.isNullStr(msg)) {
            msg = responseCode.getName();
        }
        return buildResponsePackage(responseCode.getValue(), msg, infoStr);
    }

    /**
     * 构建响应报文
     *
     * @param code    响应代码
     * @param msg     响应信息
     * @param infoStr 响应内容
     * @return 响应报文JSON对象
     */
    public static ObjectNode buildResponsePackage(Integer code, String msg, String infoStr) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode pkg = mapper.createObjectNode();
        pkg.put(ResponseKeys.responseCodeKey, code);
        pkg.put(ResponseKeys.responseMsgKey, msg);
        if (!CommonTools.isNullStr(infoStr)) {
            pkg.put(ResponseKeys.responseDataKey, infoStr);
        }
        return pkg;
    }

    /**
     * 构建响应报文
     *
     * @param code     响应代码
     * @param msg      响应信息
     * @param jsonNode 响应json对象
     * @return 响应报文JSON对象
     */
    public static ObjectNode buildResponsePackage(Integer code, String msg, JsonNode jsonNode) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode pkg = mapper.createObjectNode();
        pkg.put(ResponseKeys.responseCodeKey, code);
        pkg.put(ResponseKeys.responseMsgKey, msg);
        if (jsonNode != null) {
            pkg.set(ResponseKeys.responseDataKey, jsonNode);
        }
        return pkg;
    }

    /**
     * 构建响应报文
     *
     * @param responseCode 响应代码
     * @param msg          响应信息
     * @return 响应报文JSON对象
     */
    public static ObjectNode buildErrorResponsePackage(ResponseCode responseCode, String msg) {
        if (CommonTools.isNullStr(msg)) {
            msg = responseCode.getName();
        }
        return buildErrorResponsePackage(responseCode.getValue(), msg);
    }

    /**
     * 构建响应报文
     *
     * @param code 响应代码
     * @param msg  响应信息
     * @return 响应报文JSON对象
     */
    public static ObjectNode buildErrorResponsePackage(Integer code, String msg) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode pkg = mapper.createObjectNode();
        pkg.put(ResponseKeys.responseCodeKey, code);
        pkg.put(ResponseKeys.responseErrorKey, msg);
        pkg.put(ResponseKeys.responseErrorMsgKey, msg);
        return pkg;
    }

}
