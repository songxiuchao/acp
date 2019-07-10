package com.core.orm.test;

import pers.acp.core.dbcon.annotation.ADBTable;
import pers.acp.core.dbcon.annotation.ADBTableField;
import pers.acp.core.dbcon.annotation.ADBTablePrimaryKey;
import pers.acp.core.dbcon.entity.DBTable;
import pers.acp.core.dbcon.entity.DBTableFieldType;
import pers.acp.core.dbcon.entity.DBTablePrimaryKeyType;

/**
 * Create by zhangbin on 2017-8-7 1:30
 */
@ADBTable(tablename = "table1")
public class Table1 extends DBTable {

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFiled1() {
        return filed1;
    }

    public void setFiled1(String filed1) {
        this.filed1 = filed1;
    }

    @ADBTablePrimaryKey(name = "id", pKeyType = DBTablePrimaryKeyType.AutoNumber)
    private Long id;

    @ADBTableField(name = "filed1", fieldType = DBTableFieldType.String)
    private String filed1;

}
