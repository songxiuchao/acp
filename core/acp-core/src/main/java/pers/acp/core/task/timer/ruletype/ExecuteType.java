package pers.acp.core.task.timer.ruletype;

import java.util.HashMap;
import java.util.Map;

import pers.acp.core.exceptions.EnumValueUndefinedException;
import pers.acp.core.interfaces.IEnumValue;

/**
 * 执行类型
 *
 * @author zhangbin
 */
public enum ExecuteType implements IEnumValue {

    WeekDay("WeekDay", 0),

    Weekend("Weekend", 1),

    All("All", 2);

    private String name;

    private Integer value;

    private static Map<Integer, ExecuteType> map;

    private static Map<String, ExecuteType> nameMap;

    static {
        map = new HashMap<>();
        nameMap = new HashMap<>();
        for (ExecuteType type : values()) {
            map.put(type.getValue(), type);
            nameMap.put(type.getName(), type);
        }
    }

    ExecuteType(String name, Integer value) {
        this.name = name.toLowerCase();
        this.value = value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public Boolean equals(Integer value) {
        return this.value.equals(value);
    }

    public static ExecuteType getEnum(Integer value) throws EnumValueUndefinedException {
        if (map.containsKey(value)) {
            return map.get(value);
        }
        throw new EnumValueUndefinedException(ExecuteType.class, value);
    }

    public static ExecuteType getEnum(String name) throws EnumValueUndefinedException {
        if (nameMap.containsKey(name.toLowerCase())) {
            return nameMap.get(name.toLowerCase());
        }
        throw new EnumValueUndefinedException(ExecuteType.class, name);
    }

}
