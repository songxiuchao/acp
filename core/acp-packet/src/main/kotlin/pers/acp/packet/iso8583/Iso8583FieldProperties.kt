package pers.acp.packet.iso8583

import pers.acp.core.CommonTools
import pers.acp.core.base.BaseProperties
import pers.acp.core.log.LogFactory

/**
 * @author zhang by 11/07/2019
 * @since JDK 11
 */
class Iso8583FieldProperties : BaseProperties() {
    companion object {

        private val log = LogFactory.getInstance(Iso8583FieldProperties::class.java)

        /**
         * 获取数据库配置实例
         *
         * @return 数据库配置实例
         */
        @JvmStatic
        fun getInstance(): Iso8583FieldProperties? =
                try {
                    val propertiesFileName = "/iso8583.properties"
                    getInstance(Iso8583FieldProperties::class.java, propertiesFileName, CommonTools.getWebRootAbsPath() + propertiesFileName) as Iso8583FieldProperties
                } catch (e: Exception) {
                    log.error(e.message, e)
                    null
                }
    }

}
