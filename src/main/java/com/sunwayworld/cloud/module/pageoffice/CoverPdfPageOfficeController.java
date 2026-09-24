/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.pageoffice;

import com.sunwayworld.emc.framework.utils.FileSealUtils;
import com.sunwayworld.emc.module.bp.report.report.service.PfReportService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.executor.ExecutorHelper;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.utils.EncryptUtils;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileLogService;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import com.zhuozhengsoft.pageoffice.DocumentOpenType;
import com.zhuozhengsoft.pageoffice.FileMakerCtrl;
import com.zhuozhengsoft.pageoffice.FileSaver;
import com.zhuozhengsoft.pageoffice.OfficeVendorType;
import com.zhuozhengsoft.pageoffice.OpenModeType;
import com.zhuozhengsoft.pageoffice.PageOfficeCtrl;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping(value={"/open/cover/page-office"})
@Profile(value={"pageoffice"})
public class CoverPdfPageOfficeController {
    private static final Logger LOGGER = LogManager.getLogger(CoverPdfPageOfficeController.class);
    @Autowired
    private CoreFileService fileService;
    @Autowired
    private CoreFileManager fileManager;
    @Autowired
    private CoreFileLogService fileLogService;
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    @Lazy
    private PfReportService reportService;

    @RequestMapping(value={"/pdf/document"}, method={RequestMethod.GET})
    public ModelAndView wordSaveToPdf(HttpServletRequest request, Map<String, Object> map) {
        long fileId = Long.parseLong(request.getParameter("fileId"));
        String bizId = ObjectUtils.toString(request.getParameter("bizId"));
        String bizType = ObjectUtils.toString(request.getParameter("bizType"));
        String springServiceBeanName = ObjectUtils.toString(request.getParameter("springServiceBeanName"));
        CoreFileBean originFileBean = (CoreFileBean)this.fileService.selectById(fileId);
        String pdfName = FilenameUtils.removeExtension(originFileBean.getName()) + ".pdf";
        PageOfficeCtrl poCtrl = new PageOfficeCtrl(request);
        poCtrl.setCaption("\u4e09\u7ef4\u5929\u5730");
        poCtrl.setServerPage(ServletUtils.getContextPath() + "/poserver.zz");
        poCtrl.setSaveFilePage(ServletUtils.getContextPath() + "/open/cover/page-office/pdf/save?sourceFileId=" + fileId + "&bizId=" + bizId + "&bizType=" + bizType + "&springServiceBeanName=" + springServiceBeanName);
        poCtrl.setJsFunction_AfterDocumentSaved("AfterDocumentSaved()");
        poCtrl.setAllowCopy(false);
        poCtrl.setOfficeToolbars(false);
        poCtrl.addCustomToolButton("\u53e6\u5b58\u4e3aPDF\u6587\u4ef6", "SaveAsPDF()", 0);
        OpenModeType openModeType = this.getFileReadonly(originFileBean.getFileExt());
        poCtrl.setOfficeVendor(OfficeVendorType.AutoSelect);
        poCtrl.webOpen(ServletUtils.getContextPath() + "/open/page-office/file-stream?fileId=" + fileId, openModeType, "");
        map.put("pageoffice", poCtrl.getHtmlCode("PageOfficeCtrl1"));
        return new ModelAndView("module/pageoffice/pageOffice");
    }

    @RequestMapping(value={"/pdf/direct/document"})
    public void directWordSaveToPdf(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            DocumentOpenType docType;
            long fileId = Long.parseLong(request.getParameter("fileId"));
            String bizId = ObjectUtils.toString(request.getParameter("bizId"));
            String bizType = ObjectUtils.toString(request.getParameter("bizType"));
            String springServiceBeanName = ObjectUtils.toString(request.getParameter("springServiceBeanName"));
            CoreFileBean sourceCoreFileBean = (CoreFileBean)this.fileService.selectByIdIfPresent(fileId);
            if (ObjectUtils.isEmpty(sourceCoreFileBean)) {
                throw new FileException(fileId + "\u6587\u4ef6\u4e0d\u5b58\u5728");
            }
            Path sourceFilePath = CoreFileUtils.getLocalPath(sourceCoreFileBean);
            if (!sourceFilePath.toFile().exists()) {
                throw new FileException(sourceFilePath + "\u6587\u4ef6\u4e0d\u5b58\u5728");
            }
            LOGGER.info("\u7ed9\u524d\u7aef\u53d1\u8d77\u8f6c\u6362PDF\u8bf7\u6c42,\u6e90\u6587\u4ef6ID={},\u6e90\u6587\u4ef6\u8def\u5f84={}", (Object)fileId, (Object)sourceFilePath);
            FileMakerCtrl makerCtrl = new FileMakerCtrl(request);
            makerCtrl.setSaveFilePage("/open/cover/page-office/pdf/save?sourceFileId=" + fileId + "&targetId=" + sourceCoreFileBean.getTargetId() + "&bizId=" + bizId + "&bizType=" + bizType + "&springServiceBeanName=" + springServiceBeanName);
            String url = this.fileManager.getDownloadUrl(sourceCoreFileBean);
            LOGGER.info("\u7ed9\u524d\u7aef\u53d1\u8d77\u8f6c\u6362PDF\u8bf7\u6c42,url={}", (Object)url);
            if (sourceCoreFileBean.getName().endsWith(".docx") || sourceCoreFileBean.getName().endsWith(".doc")) {
                docType = DocumentOpenType.Word;
            } else if (sourceCoreFileBean.getName().endsWith(".xlsx") || sourceCoreFileBean.getName().endsWith(".xls")) {
                docType = DocumentOpenType.Excel;
            } else if (sourceCoreFileBean.getName().endsWith(".pptx") || sourceCoreFileBean.getName().endsWith(".ppt")) {
                docType = DocumentOpenType.PowerPoint;
            } else {
                throw new FileException("\u4e0d\u652f\u6301\u7684\u6587\u4ef6\u7c7b\u578b");
            }
            String pdfName = FilenameUtils.removeExtension(sourceCoreFileBean.getName()) + ".pdf";
            long startTime = System.currentTimeMillis();
            LOGGER.info("\u5f00\u59cb\u6267\u884cPageOffice\u8f6c\u6362, fileId={}, \u6587\u4ef6\u540d={}", (Object)fileId, (Object)sourceCoreFileBean.getName());
            makerCtrl.fillDocumentAsPDF(url, docType, pdfName);
            String html = makerCtrl.getHtml();
            if (StringUtils.isBlank(html)) {
                throw new FileException("PageOffice\u8f6c\u6362\u5931\u8d25\uff1a\u751f\u6210\u7684HTML\u4e3a\u7a7a, fileId=" + fileId);
            }
            LOGGER.info("PageOffice\u8f6c\u6362\u6210\u529f, fileId={}, \u8017\u65f6={}ms, HTML\u957f\u5ea6={}", (Object)fileId, (Object)(System.currentTimeMillis() - startTime), (Object)html.length());
            response.getOutputStream().print(makerCtrl.getHtml());
        }
        catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("\u53d1\u8d77\u8f6cPDF\u8bf7\u6c42\u5931\u8d25");
        }
    }

    @RequestMapping(value={"/pdf/save"})
    @Transactional
    public void savePdfFile(HttpServletRequest request, HttpServletResponse response) {
        try {
            LOGGER.error("\u8fdb\u5165\u7b2c\u4e8c\u4e2a\u8f6cPDF\u7684\u63a5\u53e3/pdf/save");
            Long sourceFileId = Long.parseLong(request.getParameter("sourceFileId"));
            String bizId = ObjectUtils.toString(request.getParameter("bizId"));
            String bizType = ObjectUtils.toString(request.getParameter("bizType"));
            String targetId = ObjectUtils.toString(request.getParameter("targetId"));
            String springServiceBeanName = ObjectUtils.toString(request.getParameter("springServiceBeanName"));
            LOGGER.info("\u5f00\u59cbPDF\u8f6c\u6362, sourceFileId={},bizId={}, bizType={}, springServiceBeanName={}", (Object)sourceFileId, (Object)bizId, (Object)bizType, (Object)springServiceBeanName);
            CoreFileBean sourceCoreFile = (CoreFileBean)this.fileService.selectById(sourceFileId);
            FileSaver fs = new FileSaver(request, response);
            fs.saveToFile(fs.getFileName());
            LOGGER.info("\u539f\u6587\u4ef6={}\u8f6c\u6362PDF\u5f00\u59cb", (Object)sourceFileId);
            CoreFileBean pdfCoreFile = new CoreFileBean();
            pdfCoreFile.setId(ApplicationContextHelper.getNextIdentity());
            pdfCoreFile.setTargetId(targetId);
            pdfCoreFile.setName(FilenameUtils.removeExtension(sourceCoreFile.getName()) + ".pdf");
            pdfCoreFile.setFileExt(FileUtils.getFileExtension(pdfCoreFile.getName()));
            pdfCoreFile.setMd5Name(EncryptUtils.MD5Encrypt(pdfCoreFile.getId() + "$" + pdfCoreFile.getName()));
            pdfCoreFile.setScope(FileScope.secure.name());
            pdfCoreFile.setSize(Long.valueOf(fs.getFileSize()));
            pdfCoreFile.setTimes(0L);
            pdfCoreFile.setBizCategory("pdfReport");
            pdfCoreFile.setExt("wordReportPdf");
            pdfCoreFile.setDownloadable("1");
            pdfCoreFile.setCreatedTime(LocalDateTime.now());
            this.fileService.getDao().insert((CoreFileBean)pdfCoreFile);
            this.fileManager.upload(pdfCoreFile, (InputStream)fs.getFileStream());
            this.fileManager.download(pdfCoreFile);
            LOGGER.info("\u7ed3\u675fPDF\u8f6c\u6362, sourceFileId={},bizId={}, bizType={}, springServiceBeanName={}", (Object)sourceFileId, (Object)bizId, (Object)bizType, (Object)springServiceBeanName);
            FileUtils.deleteQuietly(new File(fs.getFileName()));
            fs.close();
            ExecutorHelper.submitAfterCommitTransaction(() -> {
                this.reportService.signIssuePdfIfRequired(pdfCoreFile);
                this.seal(pdfCoreFile);
            });
        }
        catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("\u8f6cPDF\u5931\u8d25");
        }
    }

    private void seal(CoreFileBean coreFileBean) {
        try {
            ArrayList<CoreFileBean> fileFilters = new ArrayList<CoreFileBean>();
            CoreFileBean coreFileFilter = new CoreFileBean();
            coreFileFilter.setTargetId(coreFileBean.getTargetId());
            coreFileFilter.setExt("sealPdf");
            fileFilters.add(coreFileFilter);
            this.fileService.getDao().deleteBy(fileFilters, new String[]{"TARGETID", "EXT"});
            File originalFile = CoreFileUtils.getLocalPath(coreFileBean).toFile();
            FilePathDTO archivePdfPathDTO = CoreFileUtils.toFilePath(FileScope.temp, FileUtils.getFileBaseName(coreFileBean.getName()) + "-\u76d6\u7ae0.pdf");
            Path sealFilePath = FilePathManager.getLocalPath(archivePdfPathDTO);
            FileUtils.makeDirs(sealFilePath);
            LOGGER.info("CoverPdfPageOfficeController.FileSealUtils.seal\u5f00\u59cb");
            FileSealUtils.seal(originalFile.toString(), sealFilePath.toString(), true, originalFile.getName());
            LOGGER.info("CoverPdfPageOfficeController.FileSealUtils.seal\u7ed3\u675f");
            CoreFileBean sealCoreFile = new CoreFileBean();
            sealCoreFile.setTargetId(coreFileBean.getTargetId());
            sealCoreFile.setScope(FileScope.secure.name());
            sealCoreFile.setExt("sealPdf");
            this.fileService.upload(sealCoreFile, sealFilePath.toFile());
        }
        catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("\u76d6\u7ae0\u5931\u8d25");
        }
    }

    private OpenModeType getFileReadonly(String fileExt) {
        if (!fileExt.endsWith("xls") && !fileExt.endsWith("xlsx")) {
            if (!fileExt.endsWith("ppt") && !fileExt.endsWith("pptx")) {
                return OpenModeType.docCommentOnly;
            }
            return OpenModeType.pptReadOnly;
        }
        return OpenModeType.xlsReadOnly;
    }
}
