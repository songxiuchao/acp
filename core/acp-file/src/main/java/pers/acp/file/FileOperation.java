package pers.acp.file;

import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileOperation {

    private static final LogFactory log = LogFactory.getInstance(CommonTools.class);

    /**
     * 获取文件基路径
     *
     * @param filePath 文件绝对路径
     * @return 基路径
     */
    public static String getFileBaseURL(String filePath) {
        try {
            if (CommonTools.isNullStr(filePath)) {
                filePath = CommonTools.getProjectAbsPath();
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
        String webRootAbsPath = CommonTools.getProjectAbsPath();
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
                if (tmpFile != null && tmpFile.exists()) {
                    if (!tmpFile.delete()) {
                        log.error("delete file failed : " + tmpFile.getAbsolutePath());
                    }
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
        String webRootAdsPath = CommonTools.getProjectAbsPath();
        File file = new File(webRootAdsPath + File.separator + "files"
                + File.separator + "tmp");
        if (!file.exists()) {
            if (!file.mkdir()) {
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
        String webRootAdsPath = CommonTools.getProjectAbsPath();
        File file = new File(webRootAdsPath + File.separator + "files"
                + File.separator + "template");
        if (!file.exists()) {
            if (!file.mkdir()) {
                log.error("mkdir failed : " + file.getAbsolutePath());
            }
        }
        return file.getAbsolutePath();
    }

    /**
     * 压缩文件
     *
     * @param fileNames      需要压缩的文件路径数组，可以是全路径也可以是相对于webroot的路径
     * @param resultFileName 生成的目标文件全路径
     * @param isDeleteFile   压缩完后是否删除原文件
     * @return 目标文件绝对路径
     */
    public static String filesToZIP(String[] fileNames, String resultFileName, boolean isDeleteFile) {
        long startTime = System.currentTimeMillis();
        long endTime = 0;
        ZipOutputStream out = null;
        try {
            out = new ZipOutputStream(new FileOutputStream(resultFileName));
            for (String fileName : fileNames) {
                String filename = fileName.replace("\\", File.separator).replace("/", File.separator);
                File srcfile = new File(filename);
                compress(srcfile, out, srcfile.getName());
                if (isDeleteFile) {
                    if (!srcfile.delete()) {
                        log.error("delete file failed : " + srcfile.getAbsolutePath());
                    }
                }
            }
            out.close();
            log.info("compress success");
            endTime = System.currentTimeMillis();
            return resultFileName;
        } catch (Exception e) {
            try {
                if (out != null) {
                    out.closeEntry();
                }
                if (!new File(resultFileName).delete()) {
                    log.error("delete file failed : " + resultFileName);
                }
            } catch (Exception ex) {
                log.error("file compress Exception:" + ex.getMessage(), ex);
            }
            log.error("file compress Exception:" + e.getMessage(), e);
            endTime = System.currentTimeMillis();
            return "";
        } finally {
            log.info("time consuming : " + (endTime - startTime) + " ms");
        }
    }

    /**
     * 递归压缩
     *
     * @param sourceFile 源文件
     * @param zos        zip输出流
     * @param name       压缩后的名称
     * @throws Exception 异常
     */
    private static void compress(File sourceFile, ZipOutputStream zos, String name) throws Exception {
        byte[] buf = new byte[1024];
        FileInputStream in = null;
        try {
            if (sourceFile.isFile()) {
                zos.putNextEntry(new ZipEntry(name));
                int len;
                in = new FileInputStream(sourceFile);
                while ((len = in.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                in.close();
                in = null;
            } else {
                File[] listFiles = sourceFile.listFiles();
                if (listFiles == null || listFiles.length == 0) {
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    zos.closeEntry();
                } else {
                    for (File file : listFiles) {
                        compress(file, zos, name + "/" + file.getName());
                    }
                }
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    /**
     * 解压缩文件
     *
     * @param zipFileName  zip压缩文件名
     * @param parentFold   解压目标文件夹
     * @param isDeleteFile 解压完成是否删除压缩文件
     */
    public static void ZIPToFiles(String zipFileName, String parentFold, boolean isDeleteFile) {
        ZIPToFiles(zipFileName, parentFold, null, isDeleteFile);
    }

    /**
     * 解压缩文件
     *
     * @param zipFileName  zip压缩文件名
     * @param parentFold   解压目标文件夹
     * @param charSet      字符编码
     * @param isDeleteFile 解压完成是否删除压缩文件
     */
    public static void ZIPToFiles(String zipFileName, String parentFold, String charSet, boolean isDeleteFile) {
        long startTime = System.currentTimeMillis();
        long endTime = 0;
        ZipInputStream zin = null;
        BufferedInputStream bin = null;
        BufferedOutputStream bout = null;
        try {
            if (CommonTools.isNullStr(charSet)) {
                charSet = CommonTools.getDefaultCharset();
            }
            zin = new ZipInputStream(new FileInputStream(zipFileName), Charset.forName(charSet));
            bin = new BufferedInputStream(zin);
            File fOut;
            ZipEntry entry;
            while ((entry = zin.getNextEntry()) != null && !entry.isDirectory()) {
                fOut = new File(parentFold, entry.getName());
                if (!fOut.exists()) {
                    if (!fOut.getParentFile().mkdirs()) {
                        log.error("mkdirs failed : " + fOut.getParent());
                    }
                }
                bout = new BufferedOutputStream(new FileOutputStream(fOut));
                int b;
                while ((b = bin.read()) != -1) {
                    bout.write(b);
                }
                bout.close();
            }
            bin.close();
            zin.close();
            if (isDeleteFile) {
                if (!new File(zipFileName).delete()) {
                    log.error("delete file failed : " + zipFileName);
                }
            }
            log.info("decompress success");
            endTime = System.currentTimeMillis();
        } catch (Exception e) {
            try {
                if (bout != null) {
                    bout.close();
                }
                if (bin != null) {
                    bin.close();
                }
                if (zin != null) {
                    zin.close();
                }
            } catch (Exception ex) {
                log.error("file decompress Exception:" + ex.getMessage(), ex);
            }
            log.error("file decompress Exception:" + e.getMessage(), e);
            endTime = System.currentTimeMillis();
        } finally {
            log.info("time consuming : " + (endTime - startTime) + " ms");
        }
    }

}
