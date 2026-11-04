/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.file.service.impl;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.io.excel.impl.DefaultExcelWriter;
import com.sunwayworld.framework.io.excel.impl.DefaultSheetWriter;
import com.sunwayworld.framework.io.excel.impl.ResultSetSheetWriter;
import com.sunwayworld.framework.io.excel.support.SheetContext;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.restful.data.extractor.DownloadParamExtractor;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.service.GenericExcelBuilderService;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class DefaultExcelBuilderServiceImpl
implements GenericExcelBuilderService {
    @Autowired
    @Lazy
    private CoreFileManager fileManager;

    @Override
    public <T> GenericExcelBuilderService.DownloadExcelDTO buildExcel(DownloadParamExtractor paramExtractor, Class<? extends Object> itemClass, ResultSet rs, String exportUserId, String ... sumProperties) {
        GenericExcelBuilderService.DownloadExcelDTO excelDTO = new GenericExcelBuilderService.DownloadExcelDTO();
        SheetContext sheetContext = paramExtractor.extract();
        for (String sumProperty : sumProperties) {
            sheetContext.setSumProperty(sumProperty);
        }
        ResultSetSheetWriter sheetWriter = new ResultSetSheetWriter(sheetContext, rs, itemClass);
        DefaultExcelWriter excelWriter = new DefaultExcelWriter(sheetWriter);
        CoreUserService coreUserService = ApplicationContextHelper.getBean(CoreUserService.class);
        CoreUserBean coreUser = (CoreUserBean)coreUserService.selectById(exportUserId);
        String fileName = this.getFileName(sheetContext.getTopic(), coreUser);
        FilePathDTO filePath = CoreFileUtils.toFilePath(FileScope.export, fileName);
        File exportFile = null;
        try {
            exportFile = FileUtils.createTempFile();
            try (FileOutputStream fos = new FileOutputStream(exportFile);){
                excelWriter.write(fos);
            }
            this.fileManager.upload(filePath, exportFile);
        }
        catch (IOException ioe) {
            throw new FileException(ioe);
        }
        finally {
            FileUtils.deleteQuietly(exportFile);
        }
        excelDTO.setFileName(fileName);
        excelDTO.setUrl(this.fileManager.getDownloadUrl(filePath));
        return excelDTO;
    }

    @Override
    public <T> GenericExcelBuilderService.DownloadExcelDTO buildExcel(DownloadParamExtractor paramExtractor, Page<T> page, String exportUserId) {
        String[] sumProperties;
        GenericExcelBuilderService.DownloadExcelDTO excelDTO = new GenericExcelBuilderService.DownloadExcelDTO();
        SheetContext sheetContext = paramExtractor.extract();
        for (String sumProperty : sumProperties = paramExtractor.getJsonWrapper().getSumProperties()) {
            sheetContext.setSumProperty(sumProperty);
        }
        sheetContext.setTotalMap(page.getTotalMap());
        DefaultSheetWriter sheetWriter = new DefaultSheetWriter(sheetContext, page.getRows());
        DefaultExcelWriter excelWriter = new DefaultExcelWriter(sheetWriter);
        CoreUserService coreUserService = ApplicationContextHelper.getBean(CoreUserService.class);
        CoreUserBean coreUser = (CoreUserBean)coreUserService.selectById(exportUserId);
        String fileName = this.getFileName(sheetContext.getTopic(), coreUser);
        FilePathDTO filePath = CoreFileUtils.toFilePath(FileScope.export, fileName);
        File exportFile = null;
        try {
            exportFile = FileUtils.createTempFile();
            try (FileOutputStream fos = new FileOutputStream(exportFile);){
                excelWriter.write(fos);
            }
            this.fileManager.upload(filePath, exportFile);
        }
        catch (IOException ioe) {
            throw new FileException(ioe);
        }
        finally {
            FileUtils.deleteQuietly(exportFile);
        }
        excelDTO.setFileName(fileName);
        excelDTO.setUrl(this.fileManager.getDownloadUrl(filePath));
        return excelDTO;
    }

    private String getFileName(String topic, CoreUserBean user) {
        return topic + "-" + user.getUserName() + "-" + DateTimeUtils.formatLocalDateTime(LocalDateTime.now(), "yyyyMMddHHmmss") + ".xlsx";
    }
}
