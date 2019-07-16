package pers.acp.file;

import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileOperation {

    private static final LogFactory log = LogFactory.getInstance(FileOperation.class);

    /**
     * 获取文件基路径
     *
     * @param filePath 文件绝对路径
     * @return 基路径
     */
    public static String getFileBaseURL(String filePath) {
        try {
            if (CommonTools.isNullStr(filePath)) {
                filePath = CommonTools.getWebRootAbsPath();
            }
            return new File(filePath).toURI().toURL().toString();
        } catch (Exception e) {
            log.error("Get file baseURL is failed");
            return null;
        }
    }

    /**
     * 获取文件中的内容
     *
     * @param filePath 文件绝对路径
     * @return 内容
     */
    public static String getFileContent(String filePath) {
        FileInputStream fis = null;
        FileChannel channel = null;
        try {
            StringBuilder buff = new StringBuilder();
            fis = new FileInputStream(filePath);
            channel = fis.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while (true) {
                int size = channel.read(buffer);
                if (size == -1) {
                    break;
                }
                byte[] bt = buffer.array();
                buff.append(new String(bt, 0, size, CommonTools.getDefaultCharset()));
                buffer.clear();
            }
            return buff.toString();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            try {
                if (channel != null) {
                    channel.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName 文件名称
     * @return 扩展名（小写）
     */

    public static String getFileExt(String fileName) {
        if (fileName.lastIndexOf(".") > -1) {
            return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        } else {
            return "";
        }
    }

    /**
     * 十六进制制字符串转图片文件
     *
     * @param HexString      十六进字符串
     * @param FileName       文件名
     * @param ExtensionsName 扩展名
     * @param PathFlag       生成图片文件路径标志:0-相对于WebRoot；1-自定义
     * @param ResultPathFlag 返回文件路径标志:0-相对于WebRoot；1-绝对路径
     * @param ParentPath     生成图片所在目录
     * @param isDelete       是否异步删除临时图片
     * @return 临时图片路径
     */
    public static String HexToImage(String HexString, String FileName, String ExtensionsName, int PathFlag, int ResultPathFlag, String ParentPath, boolean isDelete) {
        String fileAbsPath;
        File tmpFile = null;
        FileOutputStream out = null;
        String webRootAbsPath = CommonTools.getWebRootAbsPath();
        try {
            if (PathFlag == 0) {
                tmpFile = new File(webRootAbsPath + File.separator + "files" + File.separator + "tmp" + File.separator + FileName + "." + ExtensionsName);
            } else {
                tmpFile = new File(ParentPath + File.separator + FileName + "." + ExtensionsName);
            }
            byte[] bytes = ByteUtils.fromHexString(HexString);
            out = new FileOutputStream(tmpFile);
            out.write(bytes);
            out.flush();
            out.close();
            if (ResultPathFlag == 0) {
                fileAbsPath = tmpFile.getAbsolutePath().replace(webRootAbsPath, "").replace(File.separator, "/");
            } else {
                fileAbsPath = tmpFile.getAbsolutePath().replace(File.separator, "/");
            }
            if (isDelete) {
                CommonTools.doDeleteFile(tmpFile, true);
            }
        } catch (Exception e) {
            log.error("generate image failed:" + e.getMessage(), e);
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e1) {
                log.error(e1.getMessage(), e1);
            } finally {
                if (tmpFile != null) {
                    CommonTools.doDeleteFile(tmpFile, false);
                }
            }
            fileAbsPath = "";
        }
        return fileAbsPath;
    }

    /**
     * 生成临时文件夹
     *
     * @return 临时文件夹绝对路径
     */
    public static String buildTmpDir() {
        String webRootAdsPath = CommonTools.getWebRootAbsPath();
        File file = new File(webRootAdsPath + File.separator + "files"
                + File.separator + "tmp");
        if (!file.exists()) {
            if (!file.mkdirs()) {
                log.error("mkdir failed : " + file.getAbsolutePath());
            }
        }
        return file.getAbsolutePath();
    }

    /**
     * 获取模板文件夹
     *
     * @return 模板文件夹绝对路径
     */
    public static String buildTemplateDir() {
        String webRootAdsPath = CommonTools.getWebRootAbsPath();
        File file = new File(webRootAdsPath + File.separator + "files"
                + File.separator + "template");
        if (!file.exists()) {
            if (!file.mkdirs()) {
                log.error("mkdir failed : " + file.getAbsolutePath());
            }
        }
        return file.getAbsolutePath();
    }

}
