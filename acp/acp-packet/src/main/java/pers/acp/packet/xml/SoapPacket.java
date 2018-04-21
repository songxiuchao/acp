package pers.acp.packet.xml;

import org.w3c.dom.Document;
import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangbin by 2018-2-1 21:47
 * @since JDK1.8
 */
public class SoapPacket {

    private static LogFactory log = LogFactory.getInstance(SoapPacket.class);

    private SoapType soapType = SoapType.SOAP_1_2;

    private String nameSpace;

    private String methodName;

    private Map<String, String> patameterMap = new HashMap<>();

    private String returnName = "return";

    public SoapPacket soapType(SoapType soapType) {
        this.soapType = soapType;
        return this;
    }

    public SoapPacket nameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
        return this;
    }

    public SoapPacket methodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public SoapPacket patameterMap(Map<String, String> patameterMap) {
        this.patameterMap = patameterMap;
        return this;
    }

    public SoapPacket returnName(String returnName) {
        this.returnName = returnName;
        return this;
    }

    /**
     * 获取数据包构建器对象
     *
     * @return 数据包构建器对象
     */
    public static SoapPacket packetBuilder() {
        return new SoapPacket();
    }

    /**
     * 构建WebService请求消息
     *
     * @return 响应信息
     */
    public SOAPMessage buildSOAPMessage() throws SOAPException {
        SOAPMessage msg = MessageFactory.newInstance(soapType.getName()).createMessage();
        SOAPEnvelope envelope = msg.getSOAPPart().getEnvelope();
        SOAPBody body = envelope.getBody();

        QName ename = new QName(nameSpace, methodName, "n1");
        SOAPBodyElement ele = body.addBodyElement(ename);
        for (Map.Entry<String, String> entry : patameterMap.entrySet()) {
            ele.addChildElement(entry.getKey()).setValue(entry.getValue());
        }
        return msg;
    }

    /**
     * 获取WebService请求报文（SOAP+XML）
     *
     * @return 信息字符串
     */
    public String getSOAPMessageString() {
        try {
            SOAPMessage msg = buildSOAPMessage();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            msg.writeTo(bout);
            return bout.toString();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

    /**
     * 获取WebService返回
     *
     * @param response 响应信息对象
     * @return 响应信息值
     */
    public String getReturnString(SOAPMessage response) throws SOAPException {
        Document doc = response.getSOAPPart().getEnvelope().getBody().extractContentAsDocument();
        return doc.getElementsByTagName(returnName).item(0).getTextContent();
    }

    /**
     * 获取WebService返回
     *
     * @param responseString 响应字符串
     * @param charset        字符集
     * @return 响应信息值
     */
    public String getReturnString(String responseString, String charset) {
        MessageFactory msgFactory;
        try {
            msgFactory = MessageFactory.newInstance(soapType.getName());
            byte[] bytes;
            if (CommonTools.isNullStr(charset)) {
                bytes = responseString.getBytes();
            } else {
                bytes = responseString.getBytes(charset);
            }
            SOAPMessage reqMsg = msgFactory.createMessage(new MimeHeaders(), new ByteArrayInputStream(bytes));
            reqMsg.saveChanges();
            return getReturnString(reqMsg);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

}
