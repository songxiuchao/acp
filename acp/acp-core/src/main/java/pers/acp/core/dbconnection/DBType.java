package pers.acp.core.dbconnection;

import pers.acp.core.exceptions.EnumValueUndefinedException;
import pers.acp.core.interfaces.IEnumValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhang on 2016/7/21.
 * 数据库类型
 */
public enum DBType implements IEnumValue {

    MySQL("mysql", 1, "`", "`", "MySQLInstance", false),

    Oracle("oracle", 2, "\"", "\"", "OracleInstance", false),

    MsSQL("mssql", 3, "[", "]", "MsSQLInstance", false),

    PostgreSQL("postgresql", 4, "\"", "\"", "PostgreSQLInstance", false);

    private String name;

    private Integer value;

    private String namePrefix;

    private String nameSuffix;

    private String instanceName;

    private boolean isNoSQL;

    private static Map<String, DBType> map;

    static {
        map = new HashMap<>();
        for (DBType type : values()) {
            map.put(type.getName().toLowerCase(), type);
        }
    }

    DBType(String name, Integer value, String namePrefix, String nameSuffix, String instanceName, boolean isNoSQL) {
        this.name = name.toLowerCase();
        this.value = value;
        this.namePrefix = namePrefix;
        this.nameSuffix = nameSuffix;
        this.instanceName = instanceName;
        this.isNoSQL = isNoSQL;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public String getNamePrefix() {
        return namePrefix;
    }

    public String getNameSuffix() {
        return nameSuffix;
    }

    public String getInstanceName() {
        return this.instanceName;
    }

    public boolean isNoSQL() {
        return isNoSQL;
    }

    @Override
    public Boolean equals(Integer value) {
        return this.value.equals(value);
    }

    public static DBType getEnum(String name) throws EnumValueUndefinedException {
        if (map.containsKey(name.toLowerCase())) {
            return map.get(name.toLowerCase());
        }
        throw new EnumValueUndefinedException(DBType.class, name);
    }

    /**
     * 格式化名称
     *
     * @param name 名称
     * @return 格式化结果
     */
    public String formatName(String name) {
        return namePrefix + name + nameSuffix;
    }

}
