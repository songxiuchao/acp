package pers.acp.webservice.message;

import java.util.HashMap;
import java.util.Map;

import javax.xml.soap.SOAPConstants;

import pers.acp.core.exceptions.EnumValueUndefinedException;

public enum SoapType {

    /**
     * soap1.1协议
     */
    SOAP_1_1(SOAPConstants.SOAP_1_1_PROTOCOL, 1),

    /**
     * soap1.2协议
     */
    SOAP_1_2(SOAPConstants.SOAP_1_2_PROTOCOL, 2);

    private String name;

    private Integer value;

    private static Map<Integer, SoapType> map;

    static {
        map = new HashMap<>();
        for (SoapType type : values()) {
            map.put(type.getValue(), type);
        }
    }

    SoapType(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }

    public Boolean equals(Integer value) {
        return this.value.equals(value);
    }

    public static SoapType getEnum(Integer value) throws EnumValueUndefinedException {
        if (map.containsKey(value)) {
            return map.get(value);
        }
        throw new EnumValueUndefinedException(SoapType.class, value + "");
    }
}
