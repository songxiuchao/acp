package pers.acp.springboot.core.endpoints;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pers.acp.springboot.core.exceptions.ServerException;
import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 * @author zhangbin by 2018-1-21 1:30
 * @since JDK 11
 */
@RestController
public class DownLoadController {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    @GetMapping("/download")
    public ResponseEntity<Object> service(HttpServletRequest request, HttpServletResponse response, @RequestParam String path, @RequestParam boolean isDelete) throws ServerException {
        if (pathFilter(path)) {
            try {
                File file = new File(CommonTools.getProjectAbsPath() + path.replace("/", File.separator).replace("\\", File.separator));
                if (!file.exists()) {
                    throw new ServerException("the file [" + path + "] is not exists");
                }
                String filename = file.getName();
                InputStream fis = new BufferedInputStream(new FileInputStream(file));
                byte[] buffer = new byte[fis.available()];
                if (fis.read(buffer) == -1) {
                    log.error("file：" + filename + " is empty");
                }
                fis.close();
                response.reset();
                response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
                response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, request.getCharacterEncoding()));
                response.setContentLength(Integer.valueOf(String.valueOf(file.length())));
                OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
                toClient.write(buffer);
                toClient.flush();
                toClient.close();
                log.debug("download file success:" + filename);
                if (isDelete) {
                    CommonTools.doDeleteFile(file, true);
                }
                return ResponseEntity.ok("download successful");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new ServerException(e.getMessage());
            }
        } else {
            throw new ServerException("download file failed,the file path is not correct");
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

}
