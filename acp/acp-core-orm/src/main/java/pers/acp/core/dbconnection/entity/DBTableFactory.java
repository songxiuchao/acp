package pers.acp.core.dbconnection.entity;

import pers.acp.core.CommonTools;
import pers.acp.core.dbconnection.ConnectionFactory;
import pers.acp.core.dbconnection.DBType;
import pers.acp.core.exceptions.DBException;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

class DBTableFactory {

    /**
     * 构建条件语句
     *
     * @param whereValues 查询条件
     * @param tableInfos  数据表信息
     * @return 条件语句
     */
    private static String buildWhereStr(ConnectionFactory dbcon, Map<String, Object> whereValues, List<DBTableInfo> tableInfos) {
        DBType dbType = dbcon.getDbType();
        StringBuilder sql = new StringBuilder(" where 1=1 ");
        if (tableInfos.size() > 1) {
            int before = 0;
            int after = 1;
            while (after < tableInfos.size()) {
                DBTableInfo bTable = tableInfos.get(before);
                DBTableInfo fTable = tableInfos.get(after);
                Map<String, DBTablePrimaryKeyInfo> pKeys = bTable.getpKeys();
                for (Entry<String, DBTablePrimaryKeyInfo> entry : pKeys.entrySet()) {
                    sql.append("and ").append(dbType.formatName(bTable.getTableName()))
                            .append(".")
                            .append(dbType.formatName(entry.getKey()))
                            .append("=")
                            .append(dbType.formatName(fTable.getTableName()))
                            .append(".")
                            .append(dbType.formatName(entry.getKey()))
                            .append(" ");
                }
                before++;
                after++;
            }
        }
        if (whereValues != null) {
            List<String> remove = new ArrayList<>();
            for (Entry<String, Object> entry : whereValues.entrySet()) {
                String tablefield = "";
                for (DBTableInfo tableInfo : tableInfos) {
                    for (Entry<String, DBTablePrimaryKeyInfo> pKey : tableInfo.getpKeys().entrySet()) {
                        if ((tableInfo.getClassName() + "." + pKey.getValue().getFieldName()).equals(entry.getKey())) {
                            tablefield = dbType.formatName(tableInfo.getTableName()) + "." + dbType.formatName(pKey.getKey());
                            break;
                        }
                    }
                    if (CommonTools.isNullStr(tablefield)) {
                        for (Entry<String, DBTableFieldInfo> field : tableInfo.getFields().entrySet()) {
                            if ((tableInfo.getClassName() + "." + field.getValue().getFieldName()).equals(entry.getKey())) {
                                tablefield = dbType.formatName(tableInfo.getTableName()) + "." + dbType.formatName(field.getKey());
                                break;
                            }
                        }
                    }
                    if (!CommonTools.isNullStr(tablefield)) {
                        break;
                    }
                }
                if (!CommonTools.isNullStr(tablefield)) {
                    if (entry.getValue() == null) {
                        sql.append("and ").append(tablefield).append(" is null ");
                    } else {
                        sql.append("and ").append(tablefield).append("=? ");
                    }
                } else {
                    remove.add(entry.getKey());
                }
            }
            remove.forEach(whereValues::remove);
        }
        return sql.toString();
    }

    /**
     * 构建where条件
     *
     * @param sql   sql语句
     * @param param 参数列表
     * @param pKeys 主键Map
     * @return 0-SQL(String),1-param(Object[])
     */
    private static Object[] appendWhere(ConnectionFactory dbcon, StringBuilder sql, ArrayList<Object> param, Map<String, DBTablePrimaryKeyInfo> pKeys) throws DBException {
        DBType dbType = dbcon.getDbType();
        sql.append(" where 1=1 ");
        for (Entry<String, DBTablePrimaryKeyInfo> entry : pKeys.entrySet()) {
            DBTablePrimaryKeyInfo pKeyInfo = entry.getValue();
            if (pKeyInfo.getValue() == null) {
                throw new DBException("primary key is null!");
            } else {
                sql.append("and ").append(dbType.formatName(pKeyInfo.getName())).append("=? ");
                param.add(pKeyInfo.getValue());
            }
        }
        Object[] result = new Object[2];
        result[0] = sql.toString();
        result[1] = param.toArray();
        return result;
    }

    /**
     * 构建查询语句
     *
     * @param whereValues 查询条件
     * @param cls         目标类
     * @param obj         实例对象
     * @param attachStr   附加语句-fremark表达式，变量：${java字段名}
     * @return [0]-字段名，[1]-表名，[2]-where条件（where 开头），[3]-主键名，[4]-param(Object[])，[5]-附加语句
     */
    private static Object[] buildSelectParam(ConnectionFactory dbcon, Map<String, Object> whereValues, Class<? extends DBTable> cls, DBTable obj, String attachStr) throws Exception {
        DBType dbType = dbcon.getDbType();
        DBTable table;
        if (obj != null) {
            table = obj;
        } else {
            table = cls.newInstance();
        }
        List<DBTableInfo> tableInfos = table.getTableInfos();
        String pkeystr = "";
        StringBuilder filedstr = new StringBuilder();
        StringBuilder tablestr = new StringBuilder();
        String wherestr = buildWhereStr(dbcon, whereValues, tableInfos);
        Map<String, String> attachMap = new HashMap<>();
        for (DBTableInfo tableinfo : tableInfos) {
            Map<String, DBTablePrimaryKeyInfo> pKeys = tableinfo.getpKeys();
            for (Entry<String, DBTablePrimaryKeyInfo> entry : pKeys.entrySet()) {
                String pkey = dbType.formatName(tableinfo.getTableName()) + "." + dbType.formatName(entry.getKey());
                if (CommonTools.isNullStr(pkeystr)) {
                    pkeystr = pkey;
                }
                filedstr.append(pkey).append(",");
                if (!attachMap.containsKey(tableinfo.getClassName() + "." + entry.getValue().getFieldName())) {
                    attachMap.put(tableinfo.getClassName() + "." + entry.getValue().getFieldName(), pkey);
                }
            }
            Map<String, DBTableFieldInfo> fields = tableinfo.getFields();
            for (Entry<String, DBTableFieldInfo> entry : fields.entrySet()) {
                String filed = dbType.formatName(tableinfo.getTableName()) + "." + dbType.formatName(entry.getKey());
                filedstr.append(filed).append(",");
                if (!attachMap.containsKey(tableinfo.getClassName() + "." + entry.getValue().getFieldName())) {
                    attachMap.put(tableinfo.getClassName() + "." + entry.getValue().getFieldName(), filed);
                }
            }
            tablestr.append(dbType.formatName(tableinfo.getTableName())).append(",");
        }
        filedstr = new StringBuilder(filedstr.substring(0, filedstr.length() - 1));
        tablestr = new StringBuilder(tablestr.substring(0, tablestr.length() - 1));
        Object[] result = new Object[6];
        result[0] = filedstr.toString();
        result[1] = tablestr.toString();
        result[2] = wherestr;
        result[3] = pkeystr;
        ArrayList<Object> param = new ArrayList<>();
        if (whereValues != null) {
            param.addAll(whereValues.entrySet().stream().filter(entry -> entry.getValue() != null).map(Entry::getValue).collect(Collectors.toList()));
        }
        if (!param.isEmpty()) {
            result[4] = param.toArray();
        } else {
            result[4] = null;
        }
        if (CommonTools.isNullStr(attachStr)) {
            result[5] = "";
        } else {
            result[5] = CommonTools.replaceVar(attachStr, attachMap);
        }
        return result;
    }

    /**
     * 构建查询语句
     *
     * @param whereValues 查询条件
     * @param cls         目标类
     * @param obj         实例对象
     * @param attachStr   附加语句
     * @return 0-SQL(String),1-param(Object[])
     */
    static Object[] buildSelectStr(ConnectionFactory dbcon, Map<String, Object> whereValues, Class<? extends DBTable> cls, DBTable obj, String attachStr) throws Exception {
        Object[] param = buildSelectParam(dbcon, whereValues, cls, obj, attachStr);
        Object[] result = new Object[2];
        result[0] = "select " + param[0] + " from " + param[1] + param[2] + " " + param[5];
        result[1] = param[4];
        return result;
    }

    /**
     * 构建插入语句
     *
     * @param tableInfo 数据表信息
     * @return 0-SQL(String),1-param(Object[])
     */
    static Object[] buildInsertStr(ConnectionFactory dbcon, DBTableInfo tableInfo) throws DBException {
        DBType dbType = dbcon.getDbType();
        Object[] result = new Object[2];
        if (tableInfo != null) {
            StringBuilder sql = new StringBuilder("insert into " + dbType.formatName(tableInfo.getTableName()) + "(");
            StringBuilder values = new StringBuilder();
            ArrayList<Object> param = new ArrayList<>();
            Map<String, DBTablePrimaryKeyInfo> pKeys = tableInfo.getpKeys();
            for (Entry<String, DBTablePrimaryKeyInfo> entry : pKeys.entrySet()) {
                DBTablePrimaryKeyInfo pKeyInfo = entry.getValue();
                if (!pKeyInfo.getpKeyType().equals(DBTablePrimaryKeyType.AutoNumber.getValue())) {
                    if (pKeyInfo.getValue() == null) {
                        throw new DBException("primary key is null!");
                    } else {
                        sql.append(dbType.formatName(pKeyInfo.getName())).append(",");
                        values.append("?,");
                        param.add(pKeyInfo.getValue());
                    }
                }
            }
            Map<String, DBTableFieldInfo> fields = tableInfo.getFields();
            Iterator<Entry<String, DBTableFieldInfo>> ifields = fields.entrySet().iterator();
            while (ifields.hasNext()) {
                Entry<String, DBTableFieldInfo> entry = ifields.next();
                DBTableFieldInfo fieldInfo = entry.getValue();
                if (!fieldInfo.getFieldType().equals(DBTableFieldType.Blob)) {
                    if (!fieldInfo.isAllowNull() && fieldInfo.getValue() == null) {
                        throw new DBException("field " + fieldInfo.getName() + " is null!");
                    } else {
                        if (ifields.hasNext()) {
                            sql.append(dbType.formatName(fieldInfo.getName())).append(",");
                            values.append("?,");
                        } else {
                            sql.append(dbType.formatName(fieldInfo.getName())).append(")");
                            values.append("?");
                        }
                        param.add(fieldInfo.getValue());
                    }
                }
            }
            result[0] = sql.substring(0, sql.length() - 1) + ") values(" + values + ")";
            result[1] = param.toArray();
        }
        return result;
    }

    /**
     * 构建更新语句
     *
     * @param tableInfo      数据表信息
     * @param updateIncludes 更新时指定只更新的字段名
     * @param updateExcludes 更新时指定不更新的字段名
     * @return 0-SQL(String),1-param(Object[])
     */
    static Object[] buildUpdateStr(ConnectionFactory dbcon, DBTableInfo tableInfo, List<String> updateIncludes, List<String> updateExcludes) throws DBException {
        DBType dbType = dbcon.getDbType();
        if (tableInfo != null) {
            StringBuilder sql = new StringBuilder("update " + dbType.formatName(tableInfo.getTableName()) + " set ");
            ArrayList<Object> param = new ArrayList<>();
            Map<String, DBTableFieldInfo> fields = tableInfo.getFields();
            for (Entry<String, DBTableFieldInfo> entry : fields.entrySet()) {
                DBTableFieldInfo fieldInfo = entry.getValue();
                if (!isFilter(fieldInfo.getFieldName(), updateIncludes, updateExcludes)) {
                    if (!fieldInfo.getFieldType().equals(DBTableFieldType.Blob)) {
                        if (!fieldInfo.isAllowNull() && fieldInfo.getValue() == null) {
                            throw new DBException("field " + fieldInfo.getName() + " is null!");
                        } else {
                            sql.append(dbType.formatName(fieldInfo.getName())).append("=?,");
                            param.add(fieldInfo.getValue());
                        }
                    }
                }
            }
            if (param.isEmpty()) {
                return null;
            } else {
                return appendWhere(dbcon, new StringBuilder(sql.substring(0, sql.length() - 1)), param, tableInfo.getpKeys());
            }
        } else {
            return null;
        }
    }

    /**
     * 构建更新语句
     *
     * @param tableInfo 数据表信息
     * @return 0-SQL(String),1-param(Object[])
     */
    static Object[] buildDeleteStr(ConnectionFactory dbcon, DBTableInfo tableInfo) throws DBException {
        DBType dbType = dbcon.getDbType();
        Object[] result = new Object[2];
        if (tableInfo != null) {
            StringBuilder sql = new StringBuilder("delete from ").append(dbType.formatName(tableInfo.getTableName()));
            result = appendWhere(dbcon, sql, new ArrayList<>(), tableInfo.getpKeys());
        }
        return result;
    }

    /**
     * 字段是否需要被过滤
     *
     * @param fieldName      字段名
     * @param updateIncludes 包含的字段数组
     * @param updateExcludes 排除的字段数组
     * @return 是否被过滤
     */
    static boolean isFilter(String fieldName, List<String> updateIncludes, List<String> updateExcludes) {
        return !((updateIncludes == null || updateIncludes.isEmpty() || CommonTools.strInList(fieldName, updateIncludes, true)) && (updateExcludes == null || updateExcludes.isEmpty() || !CommonTools.strInList(fieldName, updateExcludes, true)));
    }

}
