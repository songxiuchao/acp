package pers.acp.core.dbconnection.annotation;

import pers.acp.core.dbconnection.entity.DBTablePrimaryKeyType;

import java.lang.annotation.*;

/**
 * 主键
 *
 * @author zhang
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ADBTablePrimaryKey {

    /**
     * 字段名（大小写不敏感）
     */
    String name();

    /**
     * 主键数据类型
     */
    DBTablePrimaryKeyType pKeyType() default DBTablePrimaryKeyType.Uuid;

}
