package com.core.orm.test;

import pers.acp.core.dbcon.annotation.ADBTable;
import pers.acp.core.dbcon.annotation.ADBTableField;
import pers.acp.core.dbcon.entity.DBTableFieldType;

/**
 * Create by zhangbin on 2017-8-7 1:31
 */
@ADBTable(tablename = "table2")
public class Table2 extends Table1 {

    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }

    public String getField3() {
        return field3;
    }

    public void setField3(String field3) {
        this.field3 = field3;
    }

    @ADBTableField(name = "field2", fieldType = DBTableFieldType.String)
    private String field2;

    @ADBTableField(name = "field3", fieldType = DBTableFieldType.String)
    private String field3;

}
