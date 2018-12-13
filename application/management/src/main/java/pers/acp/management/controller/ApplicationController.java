package pers.acp.management.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.acp.management.domain.ApplicationDomain;
import pers.acp.management.entity.Application;
import pers.acp.core.CommonTools;
import pers.acp.core.FileTools;
import pers.acp.file.excel.scheme.ExcelType;
import pers.acp.springboot.core.enums.ResponseCode;
import pers.acp.springboot.core.tools.PackageTools;

import java.io.File;
import java.util.List;

/**
 * @author zhangbin by 2018-1-20 22:42
 * @since JDK1.8
 */
@Api("应用列表文件生成")
@RestController
@RequestMapping("/application")
public class ApplicationController {

    private final ApplicationDomain applicationDomain;

    @Autowired
    public ApplicationController(ApplicationDomain applicationDomain) {
        this.applicationDomain = applicationDomain;
    }

    @ApiOperation(value = "生成Excel", response = String.class)
    @PostMapping(value = "/appExcel", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> appExcel() {
        List<Application> applicationList = applicationDomain.findAll();
        String names = "appname,version,webroot,language,copyrightOwner,copyrightBegin,copyrightEnd,defaultapp,type";
        String filename = FileTools.exportToExcelByJson(ExcelType.EXCEL_TYPE_XLSX, CommonTools.objectToJson(applicationList).toString(), names,
                "应用列表[colspan=9]", "名称^版本^根路径^页面语言^版权所有者^版权开始年^版权结束年^默认应用^类型", null, true, false);
        if (CommonTools.isNullStr(filename)) {
            return ResponseEntity.ok(PackageTools.buildErrorResponsePackage(ResponseCode.otherError, "生成失败！").toString());
        } else {
            CommonTools.doDeleteFile(new File(CommonTools.getProjectAbsPath() + filename.replace("/", File.separator).replace("\\", File.separator)), true);
            return ResponseEntity.ok(PackageTools.buildResponsePackage(ResponseCode.success, "生成成功", filename).toString());
        }
    }

    @ApiOperation(value = "生成PDF", response = String.class)
    @PostMapping(value = "/appPDF", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> appPDF() {
        List<Application> applicationList = applicationDomain.findAll();
        StringBuilder sb = new StringBuilder("");
        sb.append("<html><head>");
        sb.append("<meta name=\"generator\" content=\"HTML Tidy, see www.w3.org\" />");
        sb.append("<meta http-equiv=\"Content-Type\""
                + " content=\"text/html; charset=UTF-8\" />");
        sb.append("<style type=\"text/css\" mce_bogus='1'><!--"
                + "@page{size:297mm 210mm;margin:20mm 10mm 20mm 21mm;@bottom-left { content: element(footmarke); }}"
                + "#footmarke {position: running(footmarke); width:170mm; text-align:center; }"
                + "body{font-family:SimSun;}"
                + "table{table-layout:fixed;word-break:break-strict;border:1px solid black;width:100%}"
                + ".tdhead{border:1px solid black;text-align:center;height:50px}"
                + ".tdcss{border:1px solid black;text-align:center}"
                + ".tdfoot{width:100%;border-top:1px solid black;text-align:left;padding-left:10px}"
                + "--></style>");
        sb.append("</head>");
        sb.append("<body>");
        sb.append("<div align=\"center\" style=\"margin-bottom:50px\"><span style=\"font-size:30px;font-weight:bold;\">应用信息表</span></div>");
        sb.append("<table cellspacing=\"0\" cellpadding=\"2\">");
        sb.append("<thead><tr><th class='tdhead'>名称</th><th class='tdhead'>版本</th><th class='tdhead'>根路径</th><th class='tdhead'>页面语言</th><th class='tdhead'>版权所有者</th><th class='tdhead'>版权开始年</th><th class='tdhead'>版权结束年</th><th class='tdhead'>默认应用</th><th class='tdhead'>类型</th></tr></thead>");
        sb.append("<tbody>");
        for (Application application : applicationList) {
            sb.append("<tr>");
            sb.append("<td class='tdcss'>").append(application.getAppname()).append("</td>");
            sb.append("<td class='tdcss'>").append(application.getVersion()).append("</td>");
            sb.append("<td class='tdcss'>").append(application.getWebroot()).append("</td>");
            sb.append("<td class='tdcss'>").append(application.getLanguage()).append("</td>");
            sb.append("<td class='tdcss'>").append(application.getCopyrightOwner()).append("</td>");
            sb.append("<td class='tdcss'>").append(application.getCopyrightBegin()).append("</td>");
            sb.append("<td class='tdcss'>").append(application.getCopyrightEnd()).append("</td>");
            sb.append("<td class='tdcss'>").append(application.getDefaultapp()).append("</td>");
            sb.append("<td class='tdcss'>").append(application.getType()).append("</td>");
            sb.append("</tr>");
        }
        sb.append("</tbody></table></body></html>");
        String filename = FileTools.htmlToPDF(sb.toString());
        filename = FileTools.PDFAddPageNumber(filename, "", true, 0);
        if (CommonTools.isNullStr(filename)) {
            return ResponseEntity.ok(PackageTools.buildErrorResponsePackage(ResponseCode.otherError, "生成失败！").toString());
        } else {
            CommonTools.doDeleteFile(new File(CommonTools.getProjectAbsPath() + filename.replace("/", File.separator).replace("\\", File.separator)), true);
            return ResponseEntity.ok(PackageTools.buildResponsePackage(ResponseCode.success, "生成成功", filename).toString());
        }
    }

}
