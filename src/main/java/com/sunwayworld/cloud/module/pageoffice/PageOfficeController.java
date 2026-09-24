// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.cloud.module.pageoffice;

import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.security.authentication.AuthenticationHelper;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.SpireWatermarkUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.constant.CoreFileOperation;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileLogService;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import com.zhuozhengsoft.pageoffice.BorderStyleType;
import com.zhuozhengsoft.pageoffice.FileSaver;
import com.zhuozhengsoft.pageoffice.OpenModeType;
import com.zhuozhengsoft.pageoffice.PDFCtrl;
import com.zhuozhengsoft.pageoffice.PageOfficeCtrl;
import com.zhuozhengsoft.pageoffice.ThemeType;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.io.OutputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping(value={"/open/page-office"})
@Profile(value={"pageoffice"})
public class PageOfficeController {
    private static final Logger logger = LogManager.getLogger(PageOfficeController.class);
    @Autowired
    private CoreFileService fileService;
    @Autowired
    private CoreFileManager fileManager;
    @Autowired
    private CoreFileLogService fileLogService;
    @Value(value="${sunway.config.pageoffice.toolbar.enable:false}")
    private Boolean toolbar;
    @Value(value="${sunway.config.file.watermark.enabled:false}")
    private Boolean watermarkEnable;

    @RequestMapping(value={"/document"}, method={RequestMethod.GET})
    public ModelAndView showWord(HttpServletRequest request, Map<String, Object> map) throws IOException {
        Cookie[] cookies = request.getCookies();
        String requestCookie = null;
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if ("sunway-remember-me-cookie".equals(cookie.getName())) {
                    requestCookie = cookie.getValue();
                }
            }
        }
        logger.info("pageoffice\u670d\u52a1Cookie:" + requestCookie);
        String loginUserToken = AuthenticationHelper.getLoginUserToken();
        if (StringUtils.isBlank(requestCookie) || StringUtils.isBlank(loginUserToken) || !StringUtils.equals(requestCookie, loginUserToken)) {
            throw new AuthenticationServiceException("core.security.login.unauthorized");
        }
        String readonly = request.getParameter("readonly");
        if (!StringUtils.isEmpty(request.getParameter("fileId"))) {
            long fileId = Long.parseLong(request.getParameter("fileId"));
            CoreFileBean coreFile = (CoreFileBean)this.fileService.selectById(fileId);
            FilePathDTO filePathDTO = CoreFileUtils.toFilePath(coreFile);
            if (this.watermarkEnable.booleanValue()) {
                String fileBaseName = FileUtils.getFileBaseName(filePathDTO.getFileName());
                String fileExt = FileUtils.getFileExtension(filePathDTO.getFileName());
                String bakName = fileBaseName + "_REVERT_WATERMARK_BAK." + fileExt;
                FilePathDTO bakFilePathDTO = FilePathDTO.of(filePathDTO.getScope().name(), filePathDTO.getTimestamp(), filePathDTO.getIrregularName(), bakName);
                Path bakFilePath = FilePathManager.getLocalPath(bakFilePathDTO);
                if (Files.exists(bakFilePath, new LinkOption[0])) {
                    SpireWatermarkUtils.revertBakSourceFile(filePathDTO);
                }
            }
            this.fileManager.download(coreFile);
            if ("pdf".equalsIgnoreCase(coreFile.getFileExt())) {
                this.openPdfFileByPageOffice(request, map, filePathDTO);
            } else {
                PageOfficeCtrl poCtrl = new PageOfficeCtrl(request);
                poCtrl.setCustomToolbar(false);
                poCtrl.setCaption("\u4e09\u7ef4\u5929\u5730");
                poCtrl.setServerPage("/poserver.zz");
                poCtrl.setSaveFilePage("/open/page-office/save?businessId=" + fileId);
                poCtrl.setJsFunction_AfterDocumentSaved("AfterDocumentSaved()");
                poCtrl.setAllowCopy(true);
                poCtrl.setTheme(ThemeType.Office2010);
                poCtrl.setBorderStyle(BorderStyleType.BorderThin);
                if (!StringUtils.isEmpty(readonly) && "true".equals(readonly)) {
                    poCtrl.setOfficeToolbars(this.toolbar);
                } else {
                    poCtrl.setCustomToolbar(true);
                    poCtrl.addCustomToolButton("\u4fdd\u5b58", "Save", 1);
                    poCtrl.addCustomToolButton("\u663e\u793a\u5de5\u5177\u680f", "showToolBar", 9);
                    poCtrl.addCustomToolButton("\u9690\u85cf\u5de5\u5177\u680f", "hideToolBar", 18);
                }
                OpenModeType openModeType = this.getFileType(coreFile.getFileExt().toLowerCase(), readonly);
                poCtrl.webOpen("/open/page-office/file-stream?fileId=" + fileId, openModeType, "");
                map.put("pageoffice", poCtrl.getHtmlCode(""));
                String pageOfficeParam = request.getParameter("pageOfficeParam");
                if (!StringUtils.isEmpty(pageOfficeParam) && !"undefined".equals(pageOfficeParam)) {
                    JSONObject jsonObject = JSONObject.parseObject(pageOfficeParam);
                    String tempId = String.valueOf(jsonObject.get("tempId"));
                    map.put("tempId", tempId);
                    String operationWidth = String.valueOf(jsonObject.get("operationWidth"));
                    if (!"undefined".equals(operationWidth) && !"null".equals(operationWidth)) {
                        map.put("operationWidth", operationWidth);
                    }
                }
            }
        } else if (!StringUtils.isEmpty(request.getParameter("fileUrl"))) {
            String fileUrl = request.getParameter("fileUrl");
            String fileExt = FileUtils.getFileExtension(fileUrl);
            if ("pdf".equalsIgnoreCase(fileExt)) {
                this.openPdfFileUrlByPageOffice(request, map, fileUrl);
            } else {
                PageOfficeCtrl poCtrl = new PageOfficeCtrl(request);
                poCtrl.setCustomToolbar(false);
                poCtrl.setCaption("\u4e09\u7ef4\u5929\u5730");
                poCtrl.setServerPage("/poserver.zz");
                poCtrl.setSaveFilePage("/open/page-office/save?fileUrl=" + fileUrl);
                poCtrl.setJsFunction_AfterDocumentSaved("AfterDocumentSaved()");
                poCtrl.setAllowCopy(true);
                poCtrl.setTheme(ThemeType.Office2010);
                poCtrl.setBorderStyle(BorderStyleType.BorderThin);
                if (!StringUtils.isEmpty(readonly) && "true".equals(readonly)) {
                    poCtrl.setOfficeToolbars(this.toolbar);
                } else {
                    poCtrl.setCustomToolbar(true);
                    poCtrl.addCustomToolButton("\u4fdd\u5b58", "Save", 1);
                    poCtrl.addCustomToolButton("\u663e\u793a\u5de5\u5177\u680f", "showToolBar", 9);
                    poCtrl.addCustomToolButton("\u9690\u85cf\u5de5\u5177\u680f", "hideToolBar", 18);
                }
                OpenModeType openModeType = this.getFileType(fileExt.toLowerCase(), readonly);
                poCtrl.webOpen(fileUrl, openModeType, "");
                map.put("pageoffice", poCtrl.getHtmlCode(""));
                String pageOfficeParam = request.getParameter("pageOfficeParam");
                if (!StringUtils.isEmpty(pageOfficeParam) && !"undefined".equals(pageOfficeParam)) {
                    JSONObject jsonObject = JSONObject.parseObject(pageOfficeParam);
                    String tempId = String.valueOf(jsonObject.get("tempId"));
                    map.put("tempId", tempId);
                    String operationWidth = String.valueOf(jsonObject.get("operationWidth"));
                    if (!"undefined".equals(operationWidth) && !"null".equals(operationWidth)) {
                        map.put("operationWidth", operationWidth);
                    }
                }
            }
        }
        ModelAndView modelAndView = new ModelAndView("module/pageoffice/pageOffice");
        modelAndView.addObject("readonly", readonly);
        return modelAndView;
    }

    @RequestMapping(value={"/file-stream"}, method={RequestMethod.GET})
    public void getFileStream(HttpServletRequest request, HttpServletResponse response) {
        if (!StringUtils.isEmpty(request.getParameter("fileId"))) {
            long fileId = Long.parseLong(request.getParameter("fileId"));
            CoreFileBean coreFile = (CoreFileBean)this.fileService.selectById(fileId);
            FilePathDTO filePathDTO = CoreFileUtils.toFilePath(coreFile);
            Path localPath = FilePathManager.getLocalPath(filePathDTO);
            try (InputStream fileInputStream = Files.newInputStream(localPath, new OpenOption[0]);
                 OutputStream outputStream = response.getOutputStream();){
                response.reset();
                response.setContentType(this.getResponseContentType(coreFile.getFileExt()));
                response.setHeader("Content-Disposition", "attachment; filename=" + StringUtils.encodeURL(filePathDTO.getFileName()));
                response.setContentLength((int)localPath.toFile().length());
                byte[] buffer = new byte[4096048];
                int size = 0;
                while ((size = fileInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, size);
                }
                outputStream.flush();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    @RequestMapping(value={"/save"})
    public void saveFile(HttpServletRequest request, HttpServletResponse response) {
        Long fileId = Long.parseLong(request.getParameter("businessId"));
        CoreFileBean coreFile = (CoreFileBean)this.fileService.selectById(fileId);
        FilePathDTO filePathDTO = CoreFileUtils.toFilePath(coreFile);
        FileSaver fs = new FileSaver(request, response);
        fs.saveToFile(coreFile.getName());
        this.fileManager.upload(filePathDTO, (InputStream)fs.getFileStream());
        try {
            File sourceFile = new File(fs.getFileName());
            if (sourceFile.exists()) {
                File targetFile = CoreFileUtils.getLocalPath(coreFile).toFile();
                Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            logger.info("\u4fdd\u5b58\u672c\u5730\u6587\u4ef6\u5931\u8d25: " + e.getMessage());
        }
        coreFile.setVersion(coreFile.getVersion() + 1L);
        this.fileService.getDao().update(coreFile, "VERSION");
        this.fileLogService.insert(fileId, CoreFileOperation.UPDATE.name());
        FileUtils.deleteQuietly(new File(coreFile.getName()));
        fs.close();
    }

    private String getResponseContentType(String fileExt) {
        if (fileExt.endsWith("xls") || fileExt.endsWith("xlsx") || fileExt.endsWith("xlsm")) {
            return "application/x-excel";
        }
        if (fileExt.endsWith("ppt") || fileExt.endsWith("pptx")) {
            return "application/ms-powerpoint";
        }
        if (fileExt.endsWith("pdf")) {
            return "application/pdf";
        }
        return "application/msword";
    }

    private OpenModeType getFileType(String fileExt, String readonly) {
        if (fileExt.endsWith("xls") || fileExt.endsWith("xlsx") || fileExt.endsWith("xlsm")) {
            return "false".equals(readonly) ? OpenModeType.xlsNormalEdit : OpenModeType.xlsReadOnly;
        }
        if (fileExt.endsWith("ppt") || fileExt.endsWith("pptx")) {
            return "false".equals(readonly) ? OpenModeType.pptNormalEdit : OpenModeType.pptReadOnly;
        }
        return "false".equals(readonly) ? OpenModeType.docAdmin : OpenModeType.docReadOnly;
    }

    private void openPdfFileByPageOffice(HttpServletRequest request, Map<String, Object> map, FilePathDTO filePathDTO) {
        if (!StringUtils.isEmpty(request.getParameter("fileId"))) {
            long fileId = Long.parseLong(request.getParameter("fileId"));
            PDFCtrl pdfCtrl = new PDFCtrl(request);
            pdfCtrl.setServerPage("/poserver.zz");
            pdfCtrl.addCustomToolButton("\u6253\u5370", "PrintFile()", 6);
            pdfCtrl.addCustomToolButton("-", "", 0);
            pdfCtrl.addCustomToolButton("\u5b9e\u9645\u5927\u5c0f", "SetPageReal()", 16);
            pdfCtrl.addCustomToolButton("\u9002\u5408\u9875\u9762", "SetPageFit()", 17);
            pdfCtrl.addCustomToolButton("\u9002\u5408\u5bbd\u5ea6", "SetPageWidth()", 18);
            pdfCtrl.addCustomToolButton("-", "", 0);
            pdfCtrl.addCustomToolButton("\u9996\u9875", "FirstPage()", 8);
            pdfCtrl.addCustomToolButton("\u4e0a\u4e00\u9875", "PreviousPage()", 9);
            pdfCtrl.addCustomToolButton("\u4e0b\u4e00\u9875", "NextPage()", 10);
            pdfCtrl.addCustomToolButton("\u5c3e\u9875", "LastPage()", 11);
            pdfCtrl.addCustomToolButton("-", "", 0);
            pdfCtrl.addCustomToolButton("\u5de6\u8f6c", "RotateLeft()", 12);
            pdfCtrl.addCustomToolButton("\u53f3\u8f6c", "RotateRight()", 13);
            pdfCtrl.addCustomToolButton("-", "", 0);
            pdfCtrl.addCustomToolButton("\u653e\u5927", "ZoomIn()", 14);
            pdfCtrl.addCustomToolButton("\u7f29\u5c0f", "ZoomOut()", 15);
            pdfCtrl.setCaption("\u4e09\u7ef4\u5929\u5730");
            pdfCtrl.setTheme(ThemeType.Office2007);
            pdfCtrl.setBorderStyle(BorderStyleType.BorderFlat);
            pdfCtrl.webOpen("/open/page-office/file-stream?fileId=" + fileId);
            map.put("pageoffice", pdfCtrl.getHtmlCode(""));
        }
    }

    private void openPdfFileUrlByPageOffice(HttpServletRequest request, Map<String, Object> map, String fileUrl) {
        PDFCtrl pdfCtrl = new PDFCtrl(request);
        pdfCtrl.setServerPage("/poserver.zz");
        pdfCtrl.setCaption("\u4e09\u7ef4\u5929\u5730");
        pdfCtrl.setTheme(ThemeType.Office2007);
        pdfCtrl.setBorderStyle(BorderStyleType.BorderFlat);
        pdfCtrl.webOpen(fileUrl);
        map.put("pageoffice", pdfCtrl.getHtmlCode(""));
    }
}
