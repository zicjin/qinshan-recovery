/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.io;

import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateBean;
import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateColumnBean;
import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateColumnDTO;
import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateDTO;
import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateSourceBean;
import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateSourceDTO;
import com.sunwayworld.cloud.lims.reporttemplate.service.LimsReportTemplateColumnService;
import com.sunwayworld.cloud.lims.reporttemplate.service.LimsReportTemplateService;
import com.sunwayworld.cloud.lims.reporttemplate.service.LimsReportTemplateSourceService;
import com.sunwayworld.cloud.lims.reporttemplate.support.util.LimsFileUtils;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.CoreFileImportResultDTO;
import com.sunwayworld.module.item.file.function.CoreFileImportFunction;
import com.sunwayworld.module.item.file.service.CoreFileService;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LimsReportTemplateImportFunction
implements CoreFileImportFunction {
    private static final Logger LOGGER = LoggerFactory.getLogger(LimsReportTemplateImportFunction.class);
    @Autowired
    private LimsReportTemplateService limsReportTemplateService;
    @Autowired
    private LimsReportTemplateSourceService limsReportTemplateSourceService;
    @Autowired
    private LimsReportTemplateColumnService limsReportTemplateColumnService;
    @Autowired
    private CoreFileService coreFileService;

    @Override
    @Transactional
    public CoreFileImportResultDTO apply(CoreFileBean coreFileBean) {
        Path zipFilePath = LimsFileUtils.getFilePath(coreFileBean);
        Path unZipFilePath = FilePathManager.getApplicationFilePath().resolve(FileScope.temp.name()).resolve("" + System.currentTimeMillis()).resolve(UUID.randomUUID().toString());
        try {
            LimsFileUtils.unZipFile(zipFilePath, unZipFilePath);
            this.importReport(unZipFilePath);
        }
        catch (IOException e) {
            throw new FileException(e);
        }
        return null;
    }

    private void importReport(Path unZipFilePath) {
        File[] files = unZipFilePath.toFile().listFiles();
        if (files == null) {
            return;
        }
        List<File> templateDirList = Arrays.asList(files);
        ArrayList insertTemplateList = new ArrayList();
        ArrayList insertSourceList = new ArrayList();
        ArrayList insertColumnList = new ArrayList();
        templateDirList.forEach(templateDir -> {
            File[] templFiles = templateDir.listFiles();
            List<File> fileList = Arrays.asList(templFiles);
            Map fileMap = fileList.stream().collect(Collectors.toMap(file -> file.isDirectory() ? "dir" : FileUtils.getFileExtension(file), Function.identity()));
            File jsonFile = (File)fileMap.get("json");
            if (jsonFile == null) {
                return;
            }
            FileInputStream in = null;
            try {
                File[] tagFileDirs;
                File templateTagFileDir;
                File templateFile;
                in = new FileInputStream(jsonFile);
                String jsonData = IOUtils.toString(in);
                LimsReportTemplateDTO templateDTO = JSONObject.parseObject(jsonData, LimsReportTemplateDTO.class);
                LOGGER.debug("read template JSON : \n{}", (Object)jsonData);
                LimsReportTemplateBean templateBean = new LimsReportTemplateBean();
                BeanUtils.copyProperties((Object)templateDTO, (Object)templateBean, "sourceList");
                LOGGER.debug("get template : \n{}", (Object)templateBean);
                insertTemplateList.add(templateBean);
                List<LimsReportTemplateSourceDTO> sourceList = templateDTO.getSourceList();
                if (!CollectionUtils.isEmpty(sourceList)) {
                    List sourceBeanList = sourceList.stream().map(sourceDTO -> {
                        LimsReportTemplateSourceBean sourceBean = new LimsReportTemplateSourceBean();
                        BeanUtils.copyProperties(sourceDTO, (Object)sourceBean, "columnList");
                        LOGGER.info("get template source: \n {}", sourceDTO);
                        List<LimsReportTemplateColumnDTO> columnsList = sourceDTO.getColumnsList();
                        if (!CollectionUtils.isEmpty(columnsList)) {
                            List columnBeanList = columnsList.stream().map(columnDTO -> {
                                LimsReportTemplateColumnBean columnBean = new LimsReportTemplateColumnBean();
                                BeanUtils.copyProperties(columnDTO, (Object)columnBean, new String[0]);
                                LOGGER.info("get template column: \n {}", columnDTO);
                                return columnBean;
                            }).collect(Collectors.toList());
                            insertColumnList.addAll(columnBeanList);
                        }
                        return sourceBean;
                    }).collect(Collectors.toList());
                    insertSourceList.addAll(sourceBeanList);
                }
                if ((templateFile = (File)fileMap.get("docx")) != null) {
                    CoreFileBean coreFileBean = new CoreFileBean();
                    coreFileBean.setFileExt(".docx");
                    coreFileBean.setName(templateFile.getName());
                    coreFileBean.setTargetId(this.limsReportTemplateService.getDao().getTable() + "$" + templateBean.getId());
                    Long fileId = this.coreFileService.upload(coreFileBean, templateFile);
                    templateBean.setTemplateFileId(fileId);
                    templateBean.setTemplateFileName(templateFile.getName());
                    LOGGER.debug("get template file: \n {}", (Object)JSONObject.toJSONString(coreFileBean));
                }
                if ((templateTagFileDir = (File)fileMap.get("dir")) != null && (tagFileDirs = templateTagFileDir.listFiles()) != null) {
                    for (File tagFileDir : tagFileDirs) {
                        File[] tagFiles = tagFileDir.listFiles();
                        if (tagFiles == null) continue;
                        File tagFile = tagFiles[0];
                        CoreFileBean tagFileBean = new CoreFileBean();
                        tagFileBean.setFileExt(".docx");
                        tagFileBean.setName(tagFile.getName());
                        tagFileBean.setBizCategory(tagFileDir.getName());
                        tagFileBean.setTargetId(this.limsReportTemplateService.getDao().getTable() + "$" + templateBean.getId());
                        this.coreFileService.upload(tagFileBean, tagFile);
                        LOGGER.debug("get template tag file: \n tag {} : {}", (Object)tagFileDir.getName(), (Object)JSONObject.toJSONString(tagFileBean));
                    }
                }
            }
            catch (IOException e) {
                LOGGER.error("resolve template file error:", e);
            }
            finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        LOGGER.info("upload template data success : \n\t total template {}", (Object)insertTemplateList.size());
        this.limsReportTemplateService.getDao().insert(insertTemplateList);
        this.limsReportTemplateSourceService.getDao().insert(insertSourceList);
        this.limsReportTemplateColumnService.getDao().insert(insertColumnList);
    }

    @Override
    public boolean test(CoreFileBean coreFile, String service) {
        return true;
    }
}
