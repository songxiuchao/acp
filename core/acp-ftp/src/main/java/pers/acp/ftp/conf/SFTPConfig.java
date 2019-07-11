package pers.acp.ftp.conf;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import pers.acp.core.base.BaseXml;
import pers.acp.core.log.LogFactory;

import java.util.List;

/**
 * Created by zhangbin on 2016/12/21.
 * SFTP服务配置
 */
@XStreamAlias("sftp-config")
public class SFTPConfig extends BaseXml {

    private static final LogFactory log = LogFactory.getInstance(SFTPConfig.class);

    public static SFTPConfig getInstance() {
        try {
            return (SFTPConfig) Load(SFTPConfig.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public List<SFTPListener> getListens() {
        return listens;
    }

    public void setListens(List<SFTPListener> listens) {
        this.listens = listens;
    }

    @XStreamImplicit(itemFieldName = "listen")
    private List<SFTPListener> listens;

}
