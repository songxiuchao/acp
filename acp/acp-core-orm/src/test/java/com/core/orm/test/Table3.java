package com.core.orm.test;

import pers.acp.core.dbconnection.annotation.ADBTable;
import pers.acp.core.dbconnection.annotation.ADBTableField;
import pers.acp.core.dbconnection.entity.DBTableFieldType;

/**
 * Create by zhangbin on 2017-8-7 1:44
 */
@ADBTable(tablename = "table3")
public class Table3 extends Table2 {

    public String getField4() {
        return field4;
    }

    public void setField4(String field4) {
        this.field4 = field4;
    }

    public String getField5() {
        return field5;
    }

    public void setField5(String field5) {
        this.field5 = field5;
    }

    @ADBTableField(name = "field4", fieldType = DBTableFieldType.String)
    private String field4;

    @ADBTableField(name = "field5", fieldType = DBTableFieldType.String)
    private String field5;

}

