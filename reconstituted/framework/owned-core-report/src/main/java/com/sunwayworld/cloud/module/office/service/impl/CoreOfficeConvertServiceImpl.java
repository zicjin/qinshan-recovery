/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.office.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.cloud.module.office.service.CoreOfficeConvertService;
import com.sunwayworld.cloud.module.office.utils.OfficeHttpUtils;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.exception.InvalidDataException;
import com.sunwayworld.framework.http.HttpClientManager;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import java.io.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class CoreOfficeConvertServiceImpl
implements CoreOfficeConvertService {
    private static final Logger logger = LogManager.getLogger(CoreOfficeConvertServiceImpl.class);
    @Lazy
    @Autowired
    private CoreFileManager fileManager;
    @Lazy
    @Autowired
    private CoreFileService coreFileService;

    @Override
    public String sunwayOfficeConvertPdf(Long fileId, MultipartFile file) {
        String officeServerUrl = ApplicationContextHelper.getEnvironment().getProperty("sunway.config.office.server.url", String.class);
        String officeIntranetUrl = ApplicationContextHelper.getEnvironment().getProperty("sunway.config.office.file.intranet.url", String.class);
        if (!StringUtils.isBlank(officeIntranetUrl)) {
            officeServerUrl = officeIntranetUrl;
        }
        String fileServerUrl = ApplicationContextHelper.getEnvironment().getProperty("sunway.config.file.server.url", String.class);
        String fileDownloadUrl = ApplicationContextHelper.getEnvironment().getProperty("sunway.config.office.file.download.url", String.class, fileServerUrl);
        String convertUrl = ServletUtils.getUrl(officeServerUrl, "/ConvertService.ashx");
        String convertType = "pdf";
        String fileName = null;
        String fileExt = null;
        JSONObject requestJson = new JSONObject();
        requestJson.put("async", (Object)false);
        requestJson.put("outputtype", (Object)convertType);
        if (!ObjectUtils.isEmpty(fileId)) {
            CoreFileBean fileBean = this.coreFileService.selectDetail(fileId);
            requestJson.put("filetype", (Object)fileBean.getFileExt());
            requestJson.put("key", (Object)(fileId + "-V" + fileBean.getVersion()));
            requestJson.put("title", (Object)fileBean.getName());
            requestJson.put("url", (Object)ServletUtils.getUrl(fileDownloadUrl, fileBean.getDownloadUrl()));
            fileName = fileBean.getName();
            fileExt = fileBean.getFileExt();
        } else if (!ObjectUtils.isEmpty(file) && file.getSize() > 0L) {
            fileName = this.getOriginalFilename(file.getOriginalFilename());
            fileExt = FileUtils.getFileExtension(fileName);
            requestJson.put("filetype", (Object)fileExt);
            requestJson.put("key", (Object)StringUtils.randomUUID());
            requestJson.put("title", (Object)fileName);
            FilePathDTO tempFilePath = CoreFileUtils.toFilePath(FileScope.temp, fileName);
            this.fileManager.upload(tempFilePath, file);
            requestJson.put("url", (Object)ServletUtils.getUrl(fileDownloadUrl, this.fileManager.getDownloadUrl(tempFilePath)));
        } else {
            throw new InvalidDataException(I18nHelper.getMessage("CORE.MODULE.OFFICE.CONVERTS.ERROR.REQUEST_PARAM_ERROR", new String[0]));
        }
        String result = OfficeHttpUtils.sendOfficeHttpPost(convertUrl, requestJson.toJSONString());
        JSONObject jsonResult = JSONObject.parseObject(result);
        if (!ObjectUtils.isEmpty(jsonResult.get("error"))) {
            logger.info(requestJson);
            logger.info(jsonResult);
            throw new InvalidDataException(I18nHelper.getMessage("CORE.MODULE.OFFICE.CONVERTS.ERROR.CONVERT_PDF_ERROR", new String[0]));
        }
        String pdfDownloadUrl = jsonResult.get("fileUrl").toString();
        File tempFile = FileUtils.createTempFile();
        HttpClientManager.getInstance().download(pdfDownloadUrl, tempFile);
        FilePathDTO pdfFilePath = CoreFileUtils.toFilePath(FileScope.temp, StringUtils.removeEnd(fileName, fileExt) + convertType);
        this.fileManager.upload(pdfFilePath, tempFile);
        return this.fileManager.getDownloadUrl(pdfFilePath);
    }

    private String getOriginalFilename(String filename) {
        int index = filename.lastIndexOf("\\");
        if (index >= 0) {
            return filename.substring(index + 1);
        }
        index = filename.lastIndexOf("/");
        if (index > 0) {
            return filename.substring(index + 1);
        }
        return filename;
    }
}
