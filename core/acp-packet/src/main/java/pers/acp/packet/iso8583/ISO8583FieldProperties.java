package pers.acp.packet.iso8583;

import pers.acp.core.config.base.BaseProperties;
import pers.acp.core.log.LogFactory;
import pers.acp.core.tools.CommonUtils;

/**
 * @author zhangbin by 2018-2-3 21:14
 * @since JDK 11
 */
public class ISO8583FieldProperties extends BaseProperties {

    private static final LogFactory log = LogFactory.getInstance(ISO8583FieldProperties.class);

    /**
     * 获取数据库配置实例
     *
     * @return 数据库配置实例
     */
    public static ISO8583FieldProperties getInstance() {
        try {
            String propertiesFileName = "/iso8583.properties";
            return (ISO8583FieldProperties) getInstance(ISO8583FieldProperties.class, propertiesFileName, CommonUtils.getWebRootAbsPath() + propertiesFileName);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

}
