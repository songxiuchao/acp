package pers.acp.core.config.base;

import pers.acp.core.exceptions.ConfigException;
import pers.acp.core.log.LogFactory;
import pers.acp.core.tools.CommonUtils;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseXML {

    private static final LogFactory log = LogFactory.getInstance(BaseXML.class);// 日志对象

    private static final Map<String, BaseXML> instanceMap = new ConcurrentHashMap<>();

    private String fileAdbPathName = null;

    private long lastModified = 0;

    /**
     * 序列化xml文件为java对象
     *
     * @param cls 序列化后转换的java类
     * @return 实例对象
     */
    @SuppressWarnings("unchecked")
    protected static BaseXML Load(Class<? extends BaseXML> cls) throws ConfigException {
        String fileName = null;
        try {
            fileName = CommonUtils.getProperties(cls.getCanonicalName());
            if (!CommonUtils.isNullStr(fileName)) {
                File file = new File(CommonUtils.getAbsPath(fileName));
                InputStreamReader inputStreamReader = null;
                if (!file.exists()) {
                    fileName = fileName.replace("\\", "/");
                    if (fileName.startsWith("/")) {
                        fileName = fileName.substring(1);
                    }
                    InputStream in = BaseXML.class.getClassLoader().getResourceAsStream(fileName);
                    if (in != null) {
                        inputStreamReader = new InputStreamReader(in, CommonUtils.getDefaultCharset());
                    } else {
                        log.error(fileName + " is not found");
                    }
                } else {
                    inputStreamReader = new InputStreamReader(new FileInputStream(file), CommonUtils.getDefaultCharset());
                }

                BaseXML instance = instanceMap.get(fileName);
                if (!instanceMap.containsKey(fileName) || (file.exists() && file.lastModified() > instance.lastModified)) {
                    if (inputStreamReader != null) {
                        synchronized (instanceMap) {
                            XStream xstream = new XStream(new DomDriver());
                            xstream.addPermission(type -> type.getName().equals(cls.getName()));
                            xstream.processAnnotations(cls);
                            BaseXML obj = (BaseXML) xstream.fromXML(inputStreamReader);
                            if (obj == null) {
                                log.error("load config failed:[" + fileName + "]");
                                instanceMap.remove(fileName);
                            } else {
                                obj.fileAdbPathName = CommonUtils.getAbsPath(fileName);
                                if (file.exists()) {
                                    obj.lastModified = file.lastModified();
                                }
                                instanceMap.put(fileName, obj);
                                log.info("load [" + fileName + "] success => " + cls.getCanonicalName());
                            }
                            instanceMap.notifyAll();
                            return obj;
                        }
                    } else {
                        return null;
                    }
                } else {
                    return instance;
                }
            } else {
                log.warn("load config failed: need specify XML file for " + cls.getCanonicalName());
                return null;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ConfigException("load config failed:[" + fileName + "]");
        }
    }

    /**
     * 将java对象信息写入xml文件
     */
    public void storeToXml() throws ConfigException {
        OutputStreamWriter oFile = null;
        try {
            if (!CommonUtils.isNullStr(this.fileAdbPathName)) {
                synchronized (this) {
                    File file = new File(fileAdbPathName);
                    XStream xstream = new XStream(new DomDriver());
                    oFile = new OutputStreamWriter(new FileOutputStream(file), CommonUtils.getDefaultCharset());
                    xstream.toXML(this, oFile);
                    lastModified = file.lastModified();
                }
                log.info("write [" + fileAdbPathName + "] success => " + this.getClass().getCanonicalName());
            } else {
                throw new ConfigException("write config failed: need specify XML file for " + this.getClass().getCanonicalName());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ConfigException("write config failed:[" + fileAdbPathName + "]");
        } finally {
            if (oFile != null) {
                try {
                    oFile.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    public String getFileAdbPathName() {
        return fileAdbPathName;
    }

    public void setFileAdbPathName(String fileAdbPathName) {
        this.fileAdbPathName = fileAdbPathName;
    }

}
