package pers.acp.core.config.instance;

import pers.acp.core.config.base.BaseProperties;
import pers.acp.core.log.LogFactory;
import pers.acp.core.tools.CommonUtils;

/**
 * Create by zhangbin on 2017-8-30 9:26
 */
public class AcpProperties extends BaseProperties {

    private static final LogFactory log = LogFactory.getInstance(AcpProperties.class);

    /**
     * 获取数据库配置实例
     *
     * @return 数据库配置实例
     */
    public static AcpProperties getInstance() {
        try {
            String propertiesFileName = "/acp.properties";
            return (AcpProperties) getInstance(AcpProperties.class, propertiesFileName, CommonUtils.getWebRootAbsPath() + propertiesFileName);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

}
