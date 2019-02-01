package pers.acp.springboot.core.component;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import pers.acp.springboot.core.exceptions.ServerException;
import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zhangbin by 2018-1-21 1:30
 * @since JDK 11
 */
@Component
public class FileDownLoadHandle {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    public void downLoadForWeb(HttpServletRequest request, HttpServletResponse response, String path, boolean isDelete) throws ServerException {
        downLoadForWeb(request, response, path, isDelete, null);
    }

    public void downLoadForWeb(HttpServletRequest request, HttpServletResponse response, String path, boolean isDelete, List<String> allowPathRegexList) throws ServerException {
        downLoadFile(request, response, CommonTools.getProjectAbsPath() + path.replace("/", File.separator).replace("\\", File.separator), isDelete, allowPathRegexList);
    }

    public void downLoadFile(HttpServletRequest request, HttpServletResponse response, String filePath, boolean isDelete, List<String> allowPathRegexList) throws ServerException {
        List<String> filterRegex = new ArrayList<>();
        if (allowPathRegexList == null || allowPathRegexList.isEmpty()) {
            filterRegex.addAll(Arrays.asList(CommonTools.getProjectAbsPath() + "/files/tmp/", CommonTools.getProjectAbsPath() + "/files/upload/", CommonTools.getProjectAbsPath() + "/files/download/"));
        } else {
            filterRegex.addAll(allowPathRegexList);
        }
        if (pathFilter(filterRegex, filePath)) {
            try {
                File file = new File(filePath);
                if (!file.exists()) {
                    throw new ServerException("the file [" + filePath + "] is not exists");
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
     * @param filterRegex 路径
     * @param path        待匹配路径
     * @return true-允许下载 false-不允许下载
     */
    private boolean pathFilter(List<String> filterRegex, String path) {
        path = path.replace("\\", "/");
        for (String regex : filterRegex) {
            if (CommonTools.regexPattern(regex.replace("\\", "/"), path)) {
                return true;
            }
        }
        return false;
    }

}
