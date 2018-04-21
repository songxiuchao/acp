package pers.acp.core.security.key.enums;

import pers.acp.core.exceptions.EnumValueUndefinedException;
import pers.acp.core.interfaces.IEnumValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhang on 2016/7/19.
 * 密钥类型
 */
public enum KeyType implements IEnumValue {

    AES("AES", 1),

    DES("DES", 2),

    DESede("3DES", 3),

    RSA("RSA", 4),

    DSA("DSA", 5),

    HMAC("HMAC", 6),

    RandomStr("RandomStr", 7),

    RandomNumber("RandomNumber", 8),

    RandomChar("RandomNumberChar", 9);

    private String name;

    private Integer value;

    private static Map<Integer, KeyType> map;

    static {
        map = new HashMap<>();
        for (KeyType type : values()) {
            map.put(type.getValue(), type);
        }
    }

    KeyType(String name, Integer value) {
        this.name = name;
        this.value = value;
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

    public static KeyType getEnum(Integer value) throws EnumValueUndefinedException {
        if (map.containsKey(value)) {
            return map.get(value);
        }
        throw new EnumValueUndefinedException(KeyType.class, value);
    }
}
