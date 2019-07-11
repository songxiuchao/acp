package pers.acp.ftp.conf;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import pers.acp.core.base.BaseXml;
import pers.acp.core.log.LogFactory;

import java.util.List;

/**
 * Created by zhangbin on 2016/12/20.
 * FTP服务配置
 */
@XStreamAlias("ftp-config")
public class FTPConfig extends BaseXml {

    private static final LogFactory log = LogFactory.getInstance(FTPConfig.class);

    public static FTPConfig getInstance() {
        try {
            return (FTPConfig) Load(FTPConfig.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public List<FTPListener> getListens() {
        return listens;
    }

    public void setListens(List<FTPListener> listens) {
        this.listens = listens;
    }

    @XStreamImplicit(itemFieldName = "listen")
    private List<FTPListener> listens;

}
