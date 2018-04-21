package pers.acp.springboot.core.handle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.MediaType;
import pers.acp.springboot.core.variable.ResponseKeys;
import pers.acp.core.log.LogFactory;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.PrintWriter;

public class HttpServletResponseAcp extends HttpServletResponseWrapper {

    private final LogFactory log = LogFactory.getInstance(this.getClass());// 日志对象

    private String oldCharset;

    public HttpServletResponseAcp(String oldCharset, HttpServletResponse response) {
        super(response);
        this.oldCharset = oldCharset;
    }

    /**
     * 返回请求信息，自动转换为客户端指定字符编码
     *
     * @param returnMessage 返回信息
     */
    public void doReturn(String returnMessage) {
        doReturn(returnMessage, MediaType.TEXT_PLAIN_VALUE);
    }

    /**
     * 返回请求信息，自动转换为客户端指定字符编码
     *
     * @param returnMessage 返回信息
     */
    public void doReturn(String returnMessage, String contentType) {
        PrintWriter writer = null;
        try {
            this.setContentType(contentType + ";charset=" + this.oldCharset);
            this.setCharacterEncoding(this.oldCharset);
            writer = this.getWriter();
            writer.write(returnMessage);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    /**
     * 返回错误信息（json格式：{"errmsg":""}），自动转换为客户端指定字符编码
     *
     * @param errorMessage 错误信息
     */
    public void doReturnError(String errorMessage) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put(ResponseKeys.responseErrorMsgKey, errorMessage);
        doReturn(objectNode.toString(), MediaType.APPLICATION_JSON_VALUE);
    }

    /**
     * 获取客户端请求时指定的字符集
     *
     * @return 字符集
     */
    public String getOldCharset() {
        return this.oldCharset;
    }
}
