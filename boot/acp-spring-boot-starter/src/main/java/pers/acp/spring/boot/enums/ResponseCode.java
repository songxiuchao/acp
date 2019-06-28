package pers.acp.spring.boot.enums;

import pers.acp.core.exceptions.EnumValueUndefinedException;
import pers.acp.core.interfaces.IEnumValue;

import java.util.HashMap;
import java.util.Map;

/**
 * 响应编码
 * Create by zhangbin on 2017-8-8 17:40
 */
public enum ResponseCode implements IEnumValue {

    success(200, "请求成功"),

    invalidParameter(422, "无效的参数"),

    authError(10001, "权限验证失败"),

    DBError(10002, "数据库错误"),

    typeMismatch(99991, "请求参数类型不正确"),

    serviceError(99997, "服务异常"),

    sysError(99998, "系统异常"),

    otherError(99999, "其他系统异常");

    ResponseCode(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    private String name;

    private Integer value;

    private static Map<Integer, ResponseCode> map;

    static {
        map = new HashMap<>();
        for (ResponseCode type : values()) {
            map.put(type.getValue(), type);
        }
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Boolean equals(Integer value) {
        return this.value.equals(value);
    }

    public static ResponseCode getEnum(Integer value) throws EnumValueUndefinedException {
        if (map.containsKey(value)) {
            return map.get(value);
        } else {
            throw new EnumValueUndefinedException(ResponseCode.class, value);
        }
    }

}
