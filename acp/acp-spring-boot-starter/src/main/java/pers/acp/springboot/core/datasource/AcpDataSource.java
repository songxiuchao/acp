package pers.acp.springboot.core.datasource;

import org.apache.tomcat.jdbc.pool.DataSource;

/**
 * @author zhangbin by 2018-1-15 15:35
 * @since JDK1.8
 */
public class AcpDataSource extends DataSource {

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public String getScanpackage() {
        return scanpackage;
    }

    public void setScanpackage(String scanpackage) {
        this.scanpackage = scanpackage;
    }

    private String dialect;

    private String scanpackage;

}
