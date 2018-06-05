package pers.acp.client.soap;

import pers.acp.client.exceptions.WSException;
import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;
import pers.acp.packet.xml.SoapPacket;
import pers.acp.packet.xml.SoapType;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import java.net.URL;
import java.util.Map;

public class WebServiceClient {

    private static int MAX_TIMEOUT = 3600000;

    private static LogFactory log = LogFactory.getInstance(WebServiceClient.class);

    private SoapType soapType = SoapType.SOAP_1_2;

    private String wsdlLocation;

    private String nameSpace;

    private String serviceName;

    private String portName;

    private String methodName;

    private Map<String, String> patameterMap;

    private String prefix = "";

    private String returnName = "return";

    private int timeout = MAX_TIMEOUT;

    /**
     * 构造函数
     *
     * @param wsdlLocation wsdl地址
     */
    public WebServiceClient(String wsdlLocation) {
        this.wsdlLocation = wsdlLocation;
    }

    /**
     * 执行WebService请求
     *
     * @return 响应信息
     */
    public String doCallWebService() {
        try {
            if (CommonTools.isNullStr(nameSpace)) {
                throw new WSException("nameSpace is null");
            }
            if (CommonTools.isNullStr(serviceName)) {
                throw new WSException("serviceName is null");
            }
            if (CommonTools.isNullStr(portName)) {
                throw new WSException("portName is null");
            }
            if (CommonTools.isNullStr(methodName)) {
                throw new WSException("methodName is null");
            }
            if (patameterMap == null || patameterMap.isEmpty()) {
                throw new WSException("patameterMap is null");
            }
            return doWSRequest();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

    /**
     * 执行WebService请求
     *
     * @return 响应信息
     */
    private String doWSRequest() throws Exception {
        if (timeout > MAX_TIMEOUT) {
            timeout = MAX_TIMEOUT;
        }
        URL url = new URL(wsdlLocation);
        QName sname = new QName(nameSpace, serviceName);
        Service service = Service.create(url, sname);

        Dispatch<SOAPMessage> dispatch = service.createDispatch(new QName(nameSpace, portName), SOAPMessage.class, Service.Mode.MESSAGE);
        dispatch.getRequestContext().put(BindingProvider.SOAPACTION_USE_PROPERTY, Boolean.TRUE);
        dispatch.getRequestContext().put(BindingProvider.SOAPACTION_URI_PROPERTY, methodName);
        dispatch.getRequestContext().put("javax.xml.ws.client.connectionTimeout", timeout);
        dispatch.getRequestContext().put("javax.xml.ws.client.receiveTimeout", timeout);
        dispatch.getRequestContext().put("thread.local.request.context", Boolean.TRUE);
        dispatch.getRequestContext().put("set-jaxb-validation-event-handler:jaxb", Boolean.TRUE);
        dispatch.getRequestContext().put("schema-validation-enabled:schema", Boolean.TRUE);
        SoapPacket soapPacket = SoapPacket.packetBuilder()
                .soapType(soapType)
                .nameSpace(nameSpace)
                .methodName(methodName)
                .patameterMap(patameterMap)
                .prefix(prefix)
                .returnName(returnName);
        SOAPMessage msg = soapPacket.buildSOAPMessage();
        log.debug("request = " + soapPacket.getSOAPMessageString(CommonTools.getDefaultCharset()));
        SOAPMessage response = dispatch.invoke(msg);
        log.debug("response = " + SoapPacket.soapMessageToString(response, CommonTools.getDefaultCharset()));
        return soapPacket.getReturnString(response);
    }

    public SoapType getSoapType() {
        return soapType;
    }

    /**
     * 设置soap协议类型，默认soap1.2
     *
     * @param soapType 协议类型
     */
    public void setSoapType(SoapType soapType) {
        this.soapType = soapType;
    }

    public String getWsdlLocation() {
        return wsdlLocation;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Map<String, String> getPatameterMap() {
        return patameterMap;
    }

    public void setPatameterMap(Map<String, String> patameterMap) {
        this.patameterMap = patameterMap;
    }

    public String getReturnName() {
        return returnName;
    }

    /**
     * 设置返回节点名称，默认“return”
     *
     * @param returnName 结果名称
     */
    public void setReturnName(String returnName) {
        this.returnName = returnName;
    }

    public int getTimeout() {
        return timeout;
    }

    /**
     * 设置请求超时时间，单位毫秒，默认1小时，最大1小时
     *
     * @param timeout 超时时间，单位毫秒
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
