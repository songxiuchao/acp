package pers.acp.core.dbconnection.entity;


import java.lang.reflect.Field;

class DBTableFieldInfo {

    /**
     * 数据库字段名
     */
    private String name;

    /**
     * java实体字段名
     */
    private String fieldName;

    private Field field;

    private Object value;

    private boolean allowNull;

    private DBTableFieldType fieldType;

    Field getField() {
        return field;
    }

    void setField(Field field) {
        this.field = field;
    }

    String getFieldName() {
        return fieldName;
    }

    void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    Object getValue() {
        return value;
    }

    void setValue(Object value) {
        this.value = value;
    }

    boolean isAllowNull() {
        return allowNull;
    }

    void setAllowNull(boolean allowNull) {
        this.allowNull = allowNull;
    }

    DBTableFieldType getFieldType() {
        return fieldType;
    }

    void setFieldType(DBTableFieldType fieldType) {
        this.fieldType = fieldType;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }
}
