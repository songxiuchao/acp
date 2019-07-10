package pers.acp.core.dbcon.entity;


import pers.acp.core.CommonTools;
import pers.acp.core.dbcon.ConnectionFactory;
import pers.acp.core.dbcon.annotation.ADBTable;
import pers.acp.core.dbcon.annotation.ADBTableField;
import pers.acp.core.dbcon.annotation.ADBTablePrimaryKey;
import pers.acp.core.exceptions.DbException;
import pers.acp.core.log.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public abstract class DBTable {

    private static final LogFactory log = LogFactory.getInstance(DBTable.class);

    /**
     * 数据表信息
     */
    private List<DBTableInfo> tableInfos = null;

    /**
     * 表名前缀
     */
    private String prefix;

    /**
     * 表名后缀
     */
    private String suffix;

    /**
     * 更新时指定只更新的字段名（java实体字段名）
     */
    private List<String> updateIncludes = new ArrayList<>();

    /**
     * 更新时指定不更新的字段名（java实体字段名）
     */
    private List<String> updateExcludes = new ArrayList<>();

    /**
     * 构造函数
     */
    protected DBTable() {
        try {
            getTableInfos();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 查询结果转换为List
     *
     * @param rs  结果集
     * @param cls 类
     * @param obj 实例对象
     * @return 结果List
     */
    private static List<DBTable> resultSetToObjList(ResultSet rs, Class<? extends DBTable> cls, DBTable obj) throws SQLException, InstantiationException, IllegalAccessException, IOException, NoSuchMethodException, InvocationTargetException {
        if (rs == null) {
            return new ArrayList<>();
        }
        if (cls == null) {
            return new ArrayList<>();
        }
        ArrayList<DBTable> list = new ArrayList<>();
        while (rs.next()) {
            DBTable rowData = rowToObj(rs, cls.getDeclaredConstructor().newInstance(), false);
            if (obj != null) {
                rowData.setPrefix(obj.getPrefix());
                rowData.setSuffix(obj.getSuffix());
            }
            list.add(rowData);
        }
        rs.close();
        return list;
    }

    /**
     * 行数据转换为对象
     *
     * @param rs              结果集
     * @param rowData         实例对象
     * @param isGeneratedKeys 是否是自动生成的键值
     * @return 实例对象
     */
    private static DBTable rowToObj(ResultSet rs, DBTable rowData, boolean isGeneratedKeys) throws IllegalAccessException, SQLException, IOException {
        ResultSetMetaData md = rs.getMetaData();
        int columnCount = md.getColumnCount();
        boolean isFind;
        Field field = null;
        DBTableFieldType fieldType = null;
        for (int i = 1; i <= columnCount; i++) {
            isFind = false;
            String colName = md.getColumnLabel(i);
            List<DBTableInfo> tableInfos = rowData.getTableInfos();
            for (DBTableInfo dbTableInfo : tableInfos) {
                Map<String, DBTablePrimaryKeyInfo> keyInfoMap = dbTableInfo.getpKeys();
                if (!isGeneratedKeys) {
                    for (Map.Entry<String, DBTablePrimaryKeyInfo> keyInfoEntry : keyInfoMap.entrySet()) {
                        DBTablePrimaryKeyInfo primaryKeyInfo = keyInfoEntry.getValue();
                        if (colName.equalsIgnoreCase(primaryKeyInfo.getName())) {
                            field = primaryKeyInfo.getField();
                            DBTablePrimaryKeyType pKeyType = primaryKeyInfo.getpKeyType();
                            if (pKeyType.equals(DBTablePrimaryKeyType.Number) || pKeyType.equals(DBTablePrimaryKeyType.AutoNumber)) {
                                fieldType = DBTableFieldType.Integer;
                            } else {
                                fieldType = DBTableFieldType.String;
                            }
                            isFind = true;
                            break;
                        }
                    }
                    if (isFind) {
                        break;
                    }
                    Map<String, DBTableFieldInfo> fieldInfoMap = dbTableInfo.getFields();
                    for (Map.Entry<String, DBTableFieldInfo> fieldInfoEntry : fieldInfoMap.entrySet()) {
                        DBTableFieldInfo fieldInfo = fieldInfoEntry.getValue();
                        if (colName.equalsIgnoreCase(fieldInfo.getName())) {
                            field = fieldInfo.getField();
                            fieldType = fieldInfo.getFieldType();
                            isFind = true;
                            break;
                        }
                    }
                } else {
                    int generatedkeyCount = 1;
                    for (Map.Entry<String, DBTablePrimaryKeyInfo> keyInfoEntry : keyInfoMap.entrySet()) {
                        DBTablePrimaryKeyInfo primaryKeyInfo = keyInfoEntry.getValue();
                        if (primaryKeyInfo.getpKeyType().equals(DBTablePrimaryKeyType.AutoNumber)) {
                            if (i == generatedkeyCount) {
                                field = primaryKeyInfo.getField();
                                fieldType = DBTableFieldType.Integer;
                                isFind = true;
                                break;
                            }
                            generatedkeyCount++;
                        }
                    }
                }
                if (isFind) {
                    break;
                }
            }
            if (isFind && field != null && fieldType != null) {
                if (DBTableFieldType.Blob.equals(fieldType)) {
                    Blob lob = rs.getBlob(i);
                    if (lob != null) {
                        field.set(rowData, lob.getBinaryStream());
                    }
                } else if (DBTableFieldType.Clob.equals(fieldType)) {
                    Clob lob = rs.getClob(i);
                    field.set(rowData, ConnectionFactory.clobToStr(lob));
                } else {
                    switch (md.getColumnType(i)) {
                        case Types.BIGINT:
                            field.set(rowData, rs.getLong(i));
                            break;
                        case Types.REAL:
                        case Types.FLOAT:
                        case Types.DOUBLE:
                            field.set(rowData, BigDecimal.valueOf(rs.getDouble(i)));
                            break;
                        default:
                            field.set(rowData, rs.getObject(i));
                    }
                }
            }
        }
        return rowData;
    }

    /**
     * 构建主键查询条件
     *
     * @param pKey 主键值
     * @param cls  实体类
     * @return 查询条件Map
     */
    private static <T> Map<String, Object> buildPkeyWhere(Object pKey, Class<T> cls) {
        try {
            Map<String, Object> whereValues = new HashMap<>();
            DBTable instance = (DBTable) cls.getDeclaredConstructor().newInstance();
            DBTableInfo dbTableInfo = instance.getTableInfos().get(0);
            Map<String, DBTablePrimaryKeyInfo> pKeys = dbTableInfo.getpKeys();
            Entry<String, DBTablePrimaryKeyInfo> entry = pKeys.entrySet().iterator().next();
            whereValues.put(dbTableInfo.getClassName() + "." + entry.getValue().getFieldName(), pKey);
            return whereValues;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 查询
     *
     * @param whereValues 查询条件，key=java类名.字段名
     * @param cls         类
     * @param obj         实例对象
     * @param attachStr   附加语句（例如：order by ${java类名.字段名}）
     * @return 结果List
     */
    private static List<DBTable> doQueryForObjList(ConnectionFactory dbcon, Map<String, Object> whereValues, Class<? extends DBTable> cls, DBTable obj, String attachStr) {
        try {
            Object[] attr = DBTableFactory.buildSelectStr(dbcon, whereValues, cls, obj, attachStr);
            ResultSet rs = dbcon.doQueryForSet(String.valueOf(attr[0]), (Object[]) attr[1]);
            if (rs != null) {
                List<DBTable> result = resultSetToObjList(rs, cls, obj);
                rs.close();
                return result;
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            log.error("query Exception:" + e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * 判断是否是合法数据表
     *
     * @return 是否是实体表
     */
    private static boolean isLegalTable(Class<?> clas) {
        if (clas == null) {
            return false;
        }
        if (clas.isAnnotationPresent(ADBTable.class)) {
            return true;
        } else {
            log.error(clas.getCanonicalName() + " need Annotation ADBTable");
            return false;
        }
    }

    /**
     * 判断当前实体是否是合法数据表
     *
     * @return 是否是实体表
     */
    private boolean isLegalTable() {
        return isLegalTable(this.getClass());
    }

    /**
     * 获取上一层实体表类
     *
     * @param currClass 类
     * @return 父类
     */
    private Class<?> getSuperClassReal(Class<?> currClass) {
        Class<?> cls = currClass.getSuperclass();
        Class<?> result = null;
        while (cls != null) {
            if (cls.isAnnotationPresent(ADBTable.class)) {
                ADBTable aTable = cls.getAnnotation(ADBTable.class);
                if (!aTable.isVirtual()) {
                    result = cls;
                    break;
                }
            } else {
                break;
            }
            cls = cls.getSuperclass();
        }
        return result;
    }

    /**
     * 判断当前类是否是顶层实体数据表
     *
     * @param currClass 类
     * @return 是否是顶层实体表
     */
    private boolean isTopDBTable(Class<?> currClass) {
        Class<?> cls = getSuperClassReal(currClass);
        return cls == null;
    }

    /**
     * 获取实体表类字段
     *
     * @param clas 实体表类
     * @return 字段集
     */
    private List<Field> getRealClassFields(Class<?> clas) {
        List<Field> result = new ArrayList<>(Arrays.asList(clas.getDeclaredFields()));
        Class<?> cls = clas.getSuperclass();
        while (cls != null && cls.isAnnotationPresent(ADBTable.class)) {
            ADBTable aTable = cls.getAnnotation(ADBTable.class);
            if (!aTable.isVirtual()) {
                break;
            }
            result.addAll(new ArrayList<>(Arrays.asList(cls.getDeclaredFields())));
            cls = cls.getSuperclass();
        }
        return result;
    }

    /**
     * 更新字段属性值
     */
    private void updateFieldsInfo() throws IllegalAccessException {
        for (DBTableInfo dbTableInfo : tableInfos) {
            Map<String, DBTablePrimaryKeyInfo> keyInfoMap = dbTableInfo.getpKeys();
            for (Entry<String, DBTablePrimaryKeyInfo> keyInfoEntry : keyInfoMap.entrySet()) {
                DBTablePrimaryKeyInfo primaryKeyInfo = keyInfoEntry.getValue();
                primaryKeyInfo.setValue(primaryKeyInfo.getField().get(this));
            }
            Map<String, DBTableFieldInfo> fieldInfoMap = dbTableInfo.getFields();
            for (Entry<String, DBTableFieldInfo> fieldInfoEntry : fieldInfoMap.entrySet()) {
                DBTableFieldInfo fieldInfo = fieldInfoEntry.getValue();
                fieldInfo.setValue(fieldInfo.getField().get(this));
            }
        }
    }

    /**
     * 获取实体表字段信息及实例内容
     *
     * @param tableInfo 表信息对象
     * @param clas      实体表类
     * @return 成功或失败
     */
    private boolean initFieldsInfo(DBTableInfo tableInfo, Class<?> clas, boolean pkonly) throws DbException, IllegalArgumentException, IllegalAccessException {
        ADBTable aTable = clas.getAnnotation(ADBTable.class);
        boolean isSeparate = aTable.isSeparate();
        List<Field> fields = getRealClassFields(clas);
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(ADBTablePrimaryKey.class)) {// 主键
                if (!isTopDBTable(clas)) {
                    throw new DbException("ADBTablePrimaryKey need used in top class!");
                }
                DBTablePrimaryKeyInfo pKeyInfo = new DBTablePrimaryKeyInfo();
                ADBTablePrimaryKey aPKey = field.getAnnotation(ADBTablePrimaryKey.class);
                if (!tableInfo.getpKeys().containsKey(aPKey.name().toUpperCase())) {
                    pKeyInfo.setName(aPKey.name().toUpperCase());
                    pKeyInfo.setFieldName(field.getName());
                    pKeyInfo.setpKeyType(aPKey.pKeyType());
                    pKeyInfo.setField(field);
                    pKeyInfo.setValue(field.get(this));
                    tableInfo.getpKeys().put(pKeyInfo.getName(), pKeyInfo);
                }
            } else if (field.isAnnotationPresent(ADBTableField.class)) {// 普通字段
                if (!pkonly) {
                    DBTableFieldInfo fieldInfo = new DBTableFieldInfo();
                    ADBTableField aField = field.getAnnotation(ADBTableField.class);
                    boolean isRepeat = false;
                    for (DBTableInfo tInfo : tableInfos) {
                        Map<String, DBTableFieldInfo> fInfo = tInfo.getFields();
                        if (fInfo.containsKey(aField.name().toUpperCase())) {
                            isRepeat = true;
                            break;
                        }
                    }
                    if (!tableInfo.getFields().containsKey(aField.name().toUpperCase()) && !isRepeat) {
                        fieldInfo.setName(aField.name().toUpperCase());
                        fieldInfo.setFieldName(field.getName());
                        fieldInfo.setFieldType(aField.fieldType());
                        fieldInfo.setAllowNull(aField.allowNull());
                        fieldInfo.setField(field);
                        fieldInfo.setValue(field.get(this));
                        tableInfo.getFields().put(fieldInfo.getName(), fieldInfo);
                    }
                }
            }
        }
        return isTopDBTable(clas) || initFieldsInfo(tableInfo, getSuperClassReal(clas), isSeparate);
    }

    /**
     * 获取表信息及实例内容
     *
     * @param clas 实体表类
     * @return 成功或失败
     */
    private boolean initTableInfo(Class<?> clas) throws DbException, IllegalArgumentException, IllegalAccessException {
        if (this.getClass().equals(clas)) {
            if (tableInfos == null) {
                tableInfos = new ArrayList<>();
            } else {
                tableInfos.clear();
            }
        }
        ADBTable aTable = clas.getAnnotation(ADBTable.class);
        if (!aTable.isVirtual()) {
            if (!CommonTools.isNullStr(aTable.tablename())) {
                DBTableInfo tableInfo = new DBTableInfo();
                String tablename = aTable.tablename();
                if (!CommonTools.isNullStr(prefix)) {
                    tablename = prefix + tablename;
                }
                if (!CommonTools.isNullStr(suffix)) {
                    tablename += suffix;
                }
                tableInfo.setTableName(tablename.toUpperCase());
                tableInfo.setClassName(clas.getCanonicalName());
                tableInfo.setSeparate(aTable.isSeparate());
                if (initFieldsInfo(tableInfo, clas, false)) {
                    if (!tableInfo.getFields().isEmpty()) {
                        tableInfos.add(tableInfo);
                    }
                    return isTopDBTable(clas) || initTableInfo(getSuperClassReal(clas));
                } else {
                    return false;
                }
            } else {
                throw new DbException(clas.getCanonicalName() + " Annotation ADBTable=>tablename is null or empty");
            }
        } else {
            return isTopDBTable(clas) || initTableInfo(getSuperClassReal(clas));
        }
    }

    /**
     * 生成UUID
     */
    private void initUuid() throws IllegalArgumentException, IllegalAccessException {
        Map<String, Object> pkey = new HashMap<>();
        Class<?> cls = this.getClass();
        while (cls != null && cls.isAnnotationPresent(ADBTable.class)) {
            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(ADBTablePrimaryKey.class)) {
                    ADBTablePrimaryKey apKeyInfo = field.getAnnotation(ADBTablePrimaryKey.class);
                    if (!pkey.containsKey(apKeyInfo.name().toUpperCase())) {
                        if (apKeyInfo.pKeyType().equals(DBTablePrimaryKeyType.Uuid)) {
                            field.set(this, CommonTools.getUuid());
                        } else if (apKeyInfo.pKeyType().equals(DBTablePrimaryKeyType.Uuid32)) {
                            field.set(this, CommonTools.getUuid32());
                        }
                        pkey.put(apKeyInfo.name().toUpperCase(), field);
                    }
                }
            }
            cls = cls.getSuperclass();
        }
    }

    /**
     * 插入记录
     *
     * @param tableInfo 数据表信息对象
     * @param dbcon     数据库连接
     * @return 成功或失败
     */
    private boolean insertTableInfo(DBTableInfo tableInfo, ConnectionFactory dbcon) throws DbException {
        Object[] attr = DBTableFactory.buildInsertStr(dbcon, tableInfo);
        return (dbcon.doUpdate(String.valueOf(attr[0]), (Object[]) attr[1])) && updateBloB(tableInfo, dbcon);
    }

    /**
     * 更新记录
     *
     * @param tableInfo 数据表信息对象
     * @param dbcon     数据库连接
     * @return 成功或失败
     */
    private boolean updateTableInfo(DBTableInfo tableInfo, ConnectionFactory dbcon) throws DbException {
        Object[] attr = DBTableFactory.buildUpdateStr(dbcon, tableInfo, updateIncludes, updateExcludes);
        return attr == null || dbcon.doUpdate(String.valueOf(attr[0]), (Object[]) attr[1]) && updateBloB(tableInfo, dbcon);
    }

    /**
     * 更新二进制大字段
     *
     * @param tableInfo 数据表信息对象
     * @param dbcon     数据库连接
     * @return 成功或失败
     */
    private boolean updateBloB(DBTableInfo tableInfo, ConnectionFactory dbcon) {
        Map<String, DBTablePrimaryKeyInfo> pKeys = tableInfo.getpKeys();
        Map<String, Object> whereValues = new HashMap<>();
        pKeys.forEach((key, value) -> whereValues.put(key, value.getValue()));
        Map<String, DBTableFieldInfo> fields = tableInfo.getFields();
        for (Entry<String, DBTableFieldInfo> entry : fields.entrySet()) {
            DBTableFieldInfo fieldInfo = entry.getValue();
            if (DBTableFactory.isFilter(fieldInfo.getFieldName(), updateIncludes, updateExcludes)) {
                if (fieldInfo.getFieldType().equals(DBTableFieldType.Blob)) {
                    if (!dbcon.doUpdateLOB(tableInfo.getTableName(), whereValues, fieldInfo.getName(), (InputStream) fieldInfo.getValue())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 更新记录
     *
     * @param tableInfo 数据表信息对象
     * @param dbcon     数据库连接
     * @return 成功或失败
     */
    private boolean deleteTableInfo(DBTableInfo tableInfo, ConnectionFactory dbcon) throws DbException {
        Object[] attr = DBTableFactory.buildDeleteStr(dbcon, tableInfo);
        return dbcon.doUpdate(String.valueOf(attr[0]), (Object[]) attr[1]);
    }

    /**
     * 获取记录实例(获取满足条件的实例)
     *
     * @param whereValues 查询条件
     * @param cls         类
     * @param obj         实例对象
     * @param dbcon       数据库连接对象
     * @param attachStr   附加语句（例如：order by）
     * @return 实体对象
     */
    public static DBTable getInstance(Map<String, Object> whereValues, Class<? extends DBTable> cls, DBTable obj, ConnectionFactory dbcon, String attachStr) {
        try {
            if (whereValues == null || whereValues.isEmpty()) {
                throw new DbException(cls.getCanonicalName() + " need primary key field");
            }
            if (isLegalTable(cls)) {
                List<DBTable> list = doQueryForObjList(dbcon, whereValues, cls, obj, attachStr);
                if (list != null && list.size() > 0) {
                    return list.get(0);
                } else {
                    return null;
                }
            } else {
                throw new DbException(cls.getCanonicalName() + " need Annotation ADBTable");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取记录实例(当有多个主键时以第一个为准)
     *
     * @param pKey 主键
     * @param cls  类
     * @param obj  实例对象
     * @return 实体对象
     */
    public static DBTable getInstance(Object pKey, Class<? extends DBTable> cls, DBTable obj) {
        return getInstance(buildPkeyWhere(pKey, cls), cls, obj);
    }

    /**
     * 获取记录实例(获取满足条件的实例)
     *
     * @param whereValues 查询条件
     * @param cls         类
     * @param obj         实例对象
     * @return 实体对象
     */
    public static DBTable getInstance(Map<String, Object> whereValues, Class<? extends DBTable> cls, DBTable obj) {
        return getInstance(whereValues, cls, obj, new ConnectionFactory());
    }

    /**
     * 获取记录实例(当有多个主键时取第一个主键)
     *
     * @param pKey  主键
     * @param cls   类
     * @param obj   实例对象
     * @param dbcon 数据库连接对象
     * @return 实体对象
     */
    public static DBTable getInstance(Object pKey, Class<? extends DBTable> cls, DBTable obj, ConnectionFactory dbcon) {
        return getInstance(buildPkeyWhere(pKey, cls), cls, obj, dbcon);
    }

    /**
     * 获取记录实例(获取满足条件的实例)
     *
     * @param whereValues 查询条件
     * @param cls         类
     * @param obj         实例对象
     * @param dbcon       数据库连接对象
     * @return 实体对象
     */
    public static DBTable getInstance(Map<String, Object> whereValues, Class<? extends DBTable> cls, DBTable obj, ConnectionFactory dbcon) {
        return getInstance(whereValues, cls, obj, dbcon, null);
    }

    /**
     * 获取记录实例(当有多个主键时取第一个主键)（查询后进行加锁）
     *
     * @param pKey  主键
     * @param cls   类
     * @param obj   实例对象
     * @param dbcon 数据库连接对象
     * @return 实体对象
     */
    public static DBTable getInstanceByLock(Object pKey, Class<? extends DBTable> cls, DBTable obj, ConnectionFactory dbcon) {
        return getInstanceByLock(buildPkeyWhere(pKey, cls), cls, obj, dbcon);
    }

    /**
     * 获取记录实例(获取满足条件的实例)（查询后进行加锁）
     *
     * @param whereValues 查询条件
     * @param cls         类
     * @param dbcon       数据库连接对象
     * @return 实体对象
     */
    public static DBTable getInstanceByLock(Map<String, Object> whereValues, Class<? extends DBTable> cls, DBTable obj, ConnectionFactory dbcon) {
        return getInstance(whereValues, cls, obj, dbcon, "for update");
    }

    /**
     * 创建记录
     *
     * @return 成功或失败
     */
    public boolean doCreate() {
        return doCreate(new ConnectionFactory());
    }

    /**
     * 创建记录
     *
     * @return 成功或失败
     */
    public boolean doCreate(ConnectionFactory dbcon) {
        boolean needCommit = false;
        try {
            initUuid();
            getTableInfos();
            if (!tableInfos.isEmpty()) {
                if (dbcon.isAutoCommit()) {
                    dbcon.beginTranslist();
                    needCommit = true;
                }
                boolean result = true;
                for (DBTableInfo tableInfo : tableInfos) {
                    if (!insertTableInfo(tableInfo, dbcon)) {
                        if (needCommit) {
                            dbcon.rollBackTranslist();
                            needCommit = false;
                        }
                        result = false;
                        break;
                    }
                }
                if (needCommit) {
                    dbcon.commitTranslist();
                }
                return result;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (needCommit) {
                dbcon.rollBackTranslist();
            }
            return false;
        }
    }

    /**
     * 更新记录
     *
     * @return 成功或失败
     */
    public boolean doUpdate() {
        return doUpdate(new ConnectionFactory());
    }

    /**
     * 更新记录
     *
     * @return 成功或失败
     */
    public boolean doUpdate(ConnectionFactory dbcon) {
        boolean needCommit = false;
        try {
            getTableInfos();
            if (!tableInfos.isEmpty()) {
                if (dbcon.isAutoCommit()) {
                    dbcon.beginTranslist();
                    needCommit = true;
                }
                boolean result = true;
                for (DBTableInfo tableInfo : tableInfos) {
                    if (!updateTableInfo(tableInfo, dbcon)) {
                        if (needCommit) {
                            dbcon.rollBackTranslist();
                            needCommit = false;
                        }
                        result = false;
                        break;
                    }
                }
                if (needCommit) {
                    dbcon.commitTranslist();
                }
                return result;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (needCommit) {
                dbcon.rollBackTranslist();
            }
            return false;
        }
    }

    /**
     * 删除记录
     *
     * @return 成功或失败
     */
    public boolean doDelete() {
        return doDelete(new ConnectionFactory());
    }

    /**
     * 删除记录
     *
     * @return 成功或失败
     */
    public boolean doDelete(ConnectionFactory dbcon) {
        boolean needCommit = false;
        try {
            getTableInfos();
            if (!tableInfos.isEmpty()) {
                if (dbcon.isAutoCommit()) {
                    dbcon.beginTranslist();
                    needCommit = true;
                }
                boolean result = true;
                for (DBTableInfo tableInfo : tableInfos) {
                    if (!deleteTableInfo(tableInfo, dbcon)) {
                        if (needCommit) {
                            dbcon.rollBackTranslist();
                            needCommit = false;
                        }
                        result = false;
                        break;
                    }
                }
                if (needCommit) {
                    dbcon.commitTranslist();
                }
                return result;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (needCommit) {
                dbcon.rollBackTranslist();
            }
            return false;
        }
    }

    /**
     * 获取实体映射信息
     *
     * @return 实体映射信息
     */
    public List<DBTableInfo> getTableInfos() {
        try {
            if (!isLegalTable()) {
                tableInfos = new ArrayList<>();
            } else {
                if (tableInfos == null || tableInfos.isEmpty()) {
                    if (!initTableInfo(this.getClass())) {
                        tableInfos = new ArrayList<>();
                    }
                } else {
                    updateFieldsInfo();
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            tableInfos = new ArrayList<>();
        }
        return tableInfos;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    /**
     * 获取当前实体映射的表名称
     *
     * @return 表名称
     */
    public String getCurrTableName() {
        getTableInfos();
        if (!tableInfos.isEmpty()) {
            return tableInfos.get(0).getTableName();
        } else {
            return "";
        }
    }

    /**
     * 获取相关联的所有表名称
     *
     * @return 表名称数组
     */
    public String[] getTableNames() {
        getTableInfos();
        List<String> names = tableInfos.stream().map(DBTableInfo::getTableName).collect(Collectors.toList());
        return names.toArray(new String[]{});
    }

    /**
     * 获取实体信息
     *
     * @return 信息字符串
     */
    public String getInfos() {
        try {
            StringBuilder sb = new StringBuilder();
            getTableInfos();
            if (!tableInfos.isEmpty()) {
                for (DBTableInfo tableInfo : tableInfos) {
                    sb.append("tablename=> ").append(tableInfo.getTableName()).append("\n");
                    tableInfo.getpKeys().forEach((key, value) -> sb.append("key=> ").append(key).append(":").append(value.getValue()).append(" type:").append(value.getpKeyType().getName()).append("\n"));
                    tableInfo.getFields().forEach((key, value) -> sb.append("field=> ").append(key).append(":").append(value.getValue()).append(" type:").append(value.getFieldType().getName()).append("\n"));
                    sb.append("\n");
                }
            }
            return sb.toString();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

    /**
     * 获取可更新的字段名（包含继承的实体表及虚拟表）
     *
     * @return 字段名数组
     */
    public String[] getRenewAbleFieldName() {
        try {
            getTableInfos();
            if (!tableInfos.isEmpty()) {
                List<String> fieldNames = new ArrayList<>();
                for (DBTableInfo tableInfo : tableInfos) {
                    Map<String, DBTableFieldInfo> fields = tableInfo.getFields();
                    fields.forEach((key, fieldInfo) -> {
                        if (DBTableFactory.isFilter(fieldInfo.getFieldName(), updateIncludes, updateExcludes)) {
                            fieldNames.add(fieldInfo.getFieldName());
                        }
                    });
                }
                return fieldNames.toArray(new String[]{});
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new String[0];
        }
    }

    /**
     * 获取更新时指定更新的字段名（java实体字段名）
     *
     * @return 字符串数组
     */
    public List<String> getUpdateIncludes() {
        return updateIncludes;
    }

    /**
     * 设置更新时指定更新的字段名（java实体字段名）
     *
     * @param updateIncludes 字符串数组
     */
    public void addUpdateIncludes(String[] updateIncludes) {
        Collections.addAll(this.updateIncludes, updateIncludes);
    }

    /**
     * 设置更新时指定不更新的字段名（java实体字段名）
     *
     * @return 字符串数组
     */
    public List<String> getUpdateExcludes() {
        return updateExcludes;
    }

    /**
     * 获取更新时指定不更新的字段名（java实体字段名）
     *
     * @param updateExcludes 字符串数组
     */
    public void addUpdateExcludes(String[] updateExcludes) {
        Collections.addAll(this.updateExcludes, updateExcludes);
    }

}
