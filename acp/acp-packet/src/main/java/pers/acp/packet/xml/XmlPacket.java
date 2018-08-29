package pers.acp.packet.xml;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author zhangbin by 2018-2-1 22:39
 * @since JDK1.8
 */
public class XmlPacket {

    private static LogFactory log = LogFactory.getInstance(XmlPacket.class);

    /**
     * 解析XML获取Map对象
     *
     * @param xml 根节点下只有一级子节点
     * @return 参数Map
     */
    public static Map<String, String> parseXML(String xml) {
        try {
            Map<String, String> result = new HashMap<>();
            JsonNode json = xmlToJson(xml);
            Iterator<Map.Entry<String, JsonNode>> iKey = json.fields();
            while (iKey.hasNext()) {
                Map.Entry<String, JsonNode> root = iKey.next();
                JsonNode rootInfo = root.getValue();
                JsonNode tags = rootInfo.get("children");
                for (JsonNode element : tags) {
                    Iterator<Map.Entry<String, JsonNode>> item = element.fields();
                    while (item.hasNext()) {
                        Map.Entry<String, JsonNode> itemnode = item.next();
                        JsonNode info = itemnode.getValue();
                        result.put(itemnode.getKey(), info.get("value").textValue());
                    }
                }
            }
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new HashMap<>();
        }
    }

    /**
     * json对象转换为xml字符串
     *
     * @param json          {tagname:{value:string,isCDATA:boolean,children:[{},{}]}}
     * @param clientCharset 字符集
     * @param isIndent      是否自动格式化
     * @return xml字符串
     */
    public static String jsonToXML(JsonNode json, String clientCharset, boolean isIndent) {
        try {
            if (json == null || json.isNull()) {
                throw new Exception("json object is null or empty");
            }
            Document document = DocumentHelper.createDocument();
            Iterator<Map.Entry<String, JsonNode>> iKey = json.fields();
            while (iKey.hasNext()) {
                Map.Entry<String, JsonNode> key = iKey.next();
                JsonNode info = key.getValue();
                Element root = document.addElement(key.getKey());
                generateXMLElementByJSON(root, info);
            }
            OutputFormat format = OutputFormat.createCompactFormat();
            format.setEncoding(clientCharset);
            format.setNewlines(isIndent);
            format.setIndent(isIndent);
            format.setIndent("    ");
            StringWriter writer = new StringWriter();
            XMLWriter output = new XMLWriter(writer, format);
            output.write(document);
            writer.close();
            output.close();
            return writer.toString();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

    /**
     * xml字符串转换为json对象
     *
     * @param xml xml字符串
     * @return {tagname:{value:string,isCDATA:boolean,children:[{},{}]}}
     */
    public static JsonNode xmlToJson(String xml) {
        try {
            SAXReader sax = new SAXReader();
            Document document = sax.read(new ByteArrayInputStream(xml.getBytes()));
            Element root = document.getRootElement();
            return generateJSONByXML(root);
        } catch (DocumentException e) {
            log.error(e.getMessage(), e);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.createObjectNode();
        }
    }

    /**
     * xml字符串转对象
     *
     * @param xmlStr xml字符串
     * @param cls    类型
     * @return 对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T xmlToObject(String xmlStr, Class<T> cls) {
        XStream xstream = new XStream(new DomDriver());
        XStream.setupDefaultSecurity(xstream);
        xstream.addPermission(type -> type.equals(cls));
        xstream.processAnnotations(cls);
        xstream.autodetectAnnotations(true);
        return (T) xstream.fromXML(xmlStr);
    }

    /**
     * 对象转xml字符串
     *
     * @param obj     对象
     * @param charset 字符集
     * @return xml字符串
     */
    public static String objectToXML(Object obj, String charset) {
        String encoding;
        if (CommonTools.isNullStr(charset)) {
            encoding = CommonTools.getDefaultCharset();
        } else {
            encoding = charset;
        }
        XStream xstream = new XStream(new DomDriver(encoding, new NoNameCoder()));
        XStream.setupDefaultSecurity(xstream);
        xstream.autodetectAnnotations(true);
        return xstream.toXML(obj);
    }

    /**
     * 通过json生成xml节点
     *
     * @param parent 父节点
     * @param obj    {tagname:{value:string,isCDATA:boolean,children:[{},{}]}}
     */
    private static void generateXMLElementByJSON(Element parent, JsonNode obj) throws Exception {
        String value = "";
        boolean isdoc = true;
        JsonNode children;
        if (obj.has("children")) {
            if (obj.get("children").isArray()) {
                children = obj.get("children");
                for (JsonNode json : children) {
                    Iterator<Map.Entry<String, JsonNode>> iKey = json.fields();
                    while (iKey.hasNext()) {
                        Map.Entry<String, JsonNode> key = iKey.next();
                        Element element = parent.addElement(key.getKey());
                        JsonNode info = key.getValue();
                        generateXMLElementByJSON(element, info);
                    }
                }
            } else {
                throw new Exception("children need jsonArray");
            }
        } else {
            if (obj.has("value")) {
                value = obj.get("value").textValue();
            }
            if (obj.has("isCDATA")) {
                isdoc = obj.get("isCDATA").asBoolean();
            }
            if (isdoc) {
                parent.addCDATA(value);
            } else {
                parent.addText(value);
            }
        }
    }

    /**
     * 生成json对象
     *
     * @param element xml元素对象
     * @return {tagname:{value:string,isCDATA:boolean,children:[{},{}]}}
     */
    private static JsonNode generateJSONByXML(Element element) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode result = mapper.createObjectNode();
        ObjectNode info = mapper.createObjectNode();
        if (!element.isTextOnly()) {
            ArrayNode jsonChildren = mapper.createArrayNode();
            List<?> children = element.elements();
            for (Object aChildren : children) {
                Element child = (Element) aChildren;
                jsonChildren.add(generateJSONByXML(child));
            }
            info.set("children", jsonChildren);
        } else {
            info.put("value", element.getTextTrim());
            if (isCDATA(element)) {
                info.put("isCDATA", true);
            } else {
                info.put("isCDATA", false);
            }
        }
        result.set(element.getName(), info);
        return result;
    }

    /**
     * 判断节点文本是否是CDATA类型
     *
     * @param node xml节点对象
     * @return 是否是CDATA类型
     */
    private static boolean isCDATA(Node node) {
        if (!node.hasContent())
            return false;
        for (Object o : ((Branch) node).content()) {
            Node n = (Node) o;
            if (Node.CDATA_SECTION_NODE == n.getNodeType()) {
                return true;
            }
        }
        return false;
    }

}
