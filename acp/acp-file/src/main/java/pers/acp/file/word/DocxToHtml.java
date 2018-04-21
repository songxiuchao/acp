package pers.acp.file.word;

import org.apache.poi.xwpf.converter.core.FileImageExtractor;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;

import java.io.*;

class DocxToHtml {

    private static final LogFactory log = LogFactory.getInstance(DocxToHtml.class);

    private static String basePath = "";

    static String convert2Html(String wordPath, String foldPath, String basePath) throws Exception {
        if (CommonTools.isNullStr(wordPath)) {
            return "";
        }
        String wordname = wordPath.substring(wordPath.lastIndexOf(File.separator) + 1, wordPath.lastIndexOf("."));
        if (!CommonTools.isNullStr(basePath)) {
            DocxToHtml.basePath = basePath;
        }
        File file = new File(wordPath);
        if (file.exists() && file.isFile()) {
            return doConvertHTMLFile(file, wordname, foldPath);
        } else {
            return "";
        }
    }

    private static String doConvertHTMLFile(File wordFile, String wordName, String foldPath) throws Exception {
        String foldpath = foldPath;

        final String prefix = CommonTools.getDateTimeString(null, "yyyyMMddHHmmssSSS");

        foldpath += File.separator + prefix;

        File fold = new File(foldpath);
        if (!fold.exists() || !fold.isDirectory()) {
            if (!fold.mkdirs()) {
                log.error("mkdirs failed : " + fold.getAbsolutePath());
            }
        }

        File baseFold;
        if (CommonTools.isNullStr(DocxToHtml.basePath)) {
            baseFold = new File(foldpath + File.separator + "img");
        } else {
            String basePath = CommonTools.getAbsPath(DocxToHtml.basePath);
            baseFold = new File(basePath + File.separator + prefix + File.separator + "img");
        }
        if (!baseFold.exists() || !baseFold.isDirectory()) {
            if (!baseFold.mkdirs()) {
                log.error("mkdirs failed : " + baseFold.getAbsolutePath());
            }
        }

        File outFile = new File(foldpath + File.separator + wordName + prefix
                + ".html");
        InputStream in = new FileInputStream(wordFile);

        XWPFDocument document = new XWPFDocument(in);
        XHTMLOptions options = XHTMLOptions.create().indent(4);
        options.setExtractor(new FileImageExtractor(baseFold));
        options.URIResolver(suggestedName -> {
            String imguri;
            if (CommonTools.isNullStr(DocxToHtml.basePath)) {
                imguri = "img/" + suggestedName;
            } else {
                imguri = DocxToHtml.basePath + "/" + prefix + "/img/" + suggestedName;
            }
            return imguri;
        });
        OutputStream out = new FileOutputStream(outFile);
        XHTMLConverter.getInstance().convert(document, out, options);

        return outFile.getAbsolutePath();
    }
}
