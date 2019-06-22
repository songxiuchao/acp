package pers.acp.webservice.conf;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * @author zhang by 21/06/2019
 * @since JDK 11
 */
public class WSServer {

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    @XStreamAsAttribute
    @XStreamAlias("class")
    private String className;

    @XStreamAsAttribute
    @XStreamAlias("href")
    private String href;

}
