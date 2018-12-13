package pers.acp.core.dbconnection.entity;


import java.lang.reflect.Field;

public class DBTablePrimaryKeyInfo {

    /**
     * 数据库字段名
     */
    private String name;

    /**
     * java实体字段名
     */
    private String fieldName;

    private DBTablePrimaryKeyType pKeyType;

    private Field field;

    private Object value;

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public DBTablePrimaryKeyType getpKeyType() {
        return pKeyType;
    }

    public void setpKeyType(DBTablePrimaryKeyType pKeyType) {
        this.pKeyType = pKeyType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}
