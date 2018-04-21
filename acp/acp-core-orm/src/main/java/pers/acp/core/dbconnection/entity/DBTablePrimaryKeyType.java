package pers.acp.core.dbconnection.entity;

import pers.acp.core.interfaces.IEnumValue;

import java.util.HashMap;
import java.util.Map;

public enum DBTablePrimaryKeyType implements IEnumValue {

    /**
     * 全球唯一字符串，36位（带分隔符-）
     */
    Uuid("Uuid", 1),

    /**
     * 全球唯一字符串，32位
     */
    Uuid32("Uuid32", 2),

    /**
     * 自增数字
     */
    AutoNumber("AutoNumber", 3),

    /**
     * 自定义字符串
     */
    String("String", 4),

    /**
     * 自定义数字
     */
    Number("Number", 5);

    private String name;

    private Integer value;

    private static Map<Integer, DBTablePrimaryKeyType> map;

    static {
        map = new HashMap<>();
        for (DBTablePrimaryKeyType type : values()) {
            map.put(type.getValue(), type);
        }
    }

    DBTablePrimaryKeyType(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Boolean equals(Integer value) {
        return this.value.equals(value);
    }

}
