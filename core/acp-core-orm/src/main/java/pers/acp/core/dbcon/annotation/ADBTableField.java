package pers.acp.core.dbcon.annotation;

import pers.acp.core.dbcon.entity.DBTableFieldType;

import java.lang.annotation.*;

/**
 * 表字段
 *
 * @author zhang
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ADBTableField {

    /**
     * 字段名（大小写不敏感）
     */
    String name();

    /**
     * 字段类型
     */
    DBTableFieldType fieldType();

    /**
     * 字段是否允许空值，默认true
     */
    boolean allowNull() default true;

}
