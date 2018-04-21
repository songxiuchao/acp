package pers.acp.core.config.instance;

import pers.acp.core.config.base.BaseProperties;
import pers.acp.core.exceptions.EnumValueUndefinedException;
import pers.acp.core.log.LogFactory;

/**
 * Create by zhangbin on 2017-8-7 17:19
 * 数据库配置
 */
public class DBProperties extends BaseProperties {

    private static final LogFactory log = LogFactory.getInstance(DBProperties.class);

    /**
     * 获取数据库配置实例
     *
     * @return 数据库配置实例
     */
    public static DBProperties getInstance() {
        try {
            String propertiesFileName = "/db.properties";
            return (DBProperties) getInstance(DBProperties.class, propertiesFileName);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取默认关系型数据库编号
     *
     * @return 数据库编号
     */
    public int getDefaultSQLDbNo() {
        return Integer.valueOf(this.getProperty("defaultsqldbno"));
    }

    /**
     * 获取数据源名称
     *
     * @param dbno 数据库编号
     * @return 数据源名称
     */
    public String getDbName(int dbno) {
        return this.getProperty("bonecp." + dbno + ".dbname");
    }

    /**
     * 获取jdbc连接字符串
     *
     * @param dbno 数据库编号
     * @return jdbc连接字符串
     */
    public String getJdbcUrlByDbNo(int dbno) {
        return this.getProperty("bonecp." + dbno + ".jdbcurl");
    }

    /**
     * 获取数据库类型
     *
     * @param dbno 数据库编号
     * @return 数据库类型
     */
    public String getDbTypeByDbNo(int dbno) throws EnumValueUndefinedException {
        return this.getProperty("bonecp." + dbno + ".dbtype");
    }

    /**
     * 获取数据库连接驱动类名
     *
     * @param dbno 数据库编号
     * @return 驱动类名
     */
    public String getDriverClass(int dbno) {
        return this.getProperty("bonecp." + dbno + ".driverclass");
    }

    /**
     * 获取数据库连接用户名
     *
     * @param dbno 数据库编号
     * @return 用户名
     */
    public String getUsernameByDbNo(int dbno) {
        return this.getProperty("bonecp." + dbno + ".username");
    }

    /**
     * 获取数据库连接密码
     *
     * @param dbno 数据库编号
     * @return 密码
     */
    public String getPasswordByDbNo(int dbno) {
        return this.getProperty("bonecp." + dbno + ".password");
    }

    /**
     * 获取数据库连接池名称
     *
     * @param dbno 数据库编号
     * @return 连接池名称s
     */
    public String getPoolNameByDbNo(int dbno) {
        return this.getProperty("bonecp." + dbno + ".poolname");
    }

}
