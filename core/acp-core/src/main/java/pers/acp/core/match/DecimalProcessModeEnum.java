package pers.acp.core.match;

import pers.acp.core.exceptions.EnumValueUndefinedException;

import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangbin on 2016/11/2.
 * 小数处理方式
 */
public enum DecimalProcessModeEnum {

    Up(1, RoundingMode.UP),//入

    Down(2, RoundingMode.DOWN),//舍

    Ceiling(3, RoundingMode.CEILING),//正无穷大

    Floor(4, RoundingMode.FLOOR),//负无穷大

    Half_UP(5, RoundingMode.HALF_UP),//四舍五入

    Half_DOWN(6, RoundingMode.HALF_DOWN),//五舍六入

    Half_EVEN(7, RoundingMode.HALF_EVEN);//银行家舍入（左为奇，四舍五入；左为偶，五舍六入）

    private Integer value;

    private RoundingMode mode;

    private static Map<Integer, DecimalProcessModeEnum> map;

    static {
        map = new HashMap<>();
        for (DecimalProcessModeEnum type : values()) {
            map.put(type.getValue(), type);
        }
    }

    DecimalProcessModeEnum(Integer value, RoundingMode mode) {
        this.value = value;
        this.mode = mode;
    }

    public Integer getValue() {
        return value;
    }

    public RoundingMode getMode() {
        return mode;
    }

    public Boolean equals(Integer value) {
        return this.value.equals(value);
    }

    public static DecimalProcessModeEnum getEnum(Integer value) throws EnumValueUndefinedException {
        if (map.containsKey(value)) {
            return map.get(value);
        } else {
            throw new EnumValueUndefinedException(DecimalProcessModeEnum.class, value);
        }
    }

}
