package pers.acp.springboot.core.endpoints;

import org.springframework.http.MediaType;
import pers.acp.springboot.core.base.BaseServlet;
import pers.acp.springboot.core.exceptions.ServerException;
import pers.acp.springboot.core.handle.HttpServletRequestAcp;
import pers.acp.springboot.core.handle.HttpServletResponseAcp;
import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import java.io.*;
import java.net.URLEncoder;

/**
 * @author zhangbin by 2018-1-21 1:30
 * @since JDK 11
 */
@WebServlet(name = "DownLoadFile", urlPatterns = "/download", initParams = {@WebInitParam(name = "encode", value = "utf-8")})
public class DownLoadFile extends BaseServlet {

    private static final long serialVersionUID = -3420753437956874984L;

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    private HttpServletRequestAcp aRequest;

    private HttpServletResponseAcp aResponse;

    @Override
    public void service(HttpServletRequestAcp aRequest, HttpServletResponseAcp aResponse) {
        this.aRequest = aRequest;
        this.aResponse = aResponse;
        try {
            String path = aRequest.getParameter("filename");
            if (pathFilter(path)) {
                download(path);
            } else {
                throw new ServerException("download file failed,the file path is not correct");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            aResponse.doReturnError(e.getMessage());
        }
    }

    /**
     * 文件路径过滤
     *
     * @param path 路径
     * @return true-允许下载 false-不允许下载
     */
    private boolean pathFilter(String path) {
        return path.startsWith("/files/tmp/") || path.startsWith("/files/upload/") || path.startsWith("/files/download/");
    }

    private void download(String path) throws Exception {
        File file = new File(CommonTools.getProjectAbsPath() + path.replace("/", File.separator).replace("\\", File.separator));
        String filename = file.getName();
        InputStream fis = new BufferedInputStream(new FileInputStream(file));
        byte[] buffer = new byte[fis.available()];
        if (fis.read(buffer) == -1) {
            log.error("file：" + filename + " is empty");
        }
        fis.close();
        aResponse.reset();
        aResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        aResponse.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, aResponse.getOldCharset()));
        aResponse.setContentLength(Integer.valueOf(String.valueOf(file.length())));
        OutputStream toClient = new BufferedOutputStream(aResponse.getOutputStream());
        toClient.write(buffer);
        toClient.flush();
        toClient.close();
        boolean isDelete = Boolean.valueOf(aRequest.getParameter("isdelete"));
        log.debug("download file success:" + filename);
        if (isDelete) {
            CommonTools.doDeleteFile(file, true);
        }
    }

}
