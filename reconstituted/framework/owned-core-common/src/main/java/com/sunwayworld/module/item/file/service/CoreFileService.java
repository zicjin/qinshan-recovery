/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.file.service;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.CoreFileCkeditorUploadResultDTO;
import com.sunwayworld.module.item.file.bean.CoreFileLogBean;
import com.sunwayworld.module.item.file.bean.CoreFileOfficeResultDTO;
import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.web.multipart.MultipartFile;

public interface CoreFileService
extends GenericService<CoreFileBean, Long> {
    @Override
    public Page<CoreFileBean> selectSearchablePagination(RestJsonWrapperBean var1);

    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long upload(CoreFileBean var1, MultipartFile var2);

    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long upload(CoreFileBean var1, File var2);

    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    default public Long upload(CoreFileBean coreFile, Path uploadPath) {
        return this.upload(coreFile, uploadPath.toFile());
    }

    public <PK extends Serializable> List<CoreFileBean> selectFileList(String var1, PK var2);

    public <PK extends Serializable> List<CoreFileBean> selectFileList(String var1, List<PK> var2);

    public List<CoreFileBean> selectFileListByTargetIds(String ... var1);

    @Override
    public void delete(RestJsonWrapperBean var1);

    public <PK extends Serializable> void delete(String var1, PK var2);

    public <PK extends Serializable> void batchDelete(String var1, List<PK> var2);

    public void save(RestJsonWrapperBean var1);

    public String pack(String var1, String var2, String var3);

    public String pack(String var1, RestJsonWrapperBean var2);

    public String pack(RestJsonWrapperBean var1);

    @Override
    public CoreFileBean selectDetail(Long var1);

    public List<CoreFileBean> copy(String var1, String var2);

    public List<CoreFileBean> copy(String var1, Consumer<CoreFileBean> var2);

    public CoreFileCkeditorUploadResultDTO ckeditorUpload(CoreFileBean var1, MultipartFile var2);

    public void deleteList(List<CoreFileBean> var1);

    public CoreFileOfficeResultDTO sunwayOfficeInvoke(Long var1);

    public CoreFileOfficeResultDTO sunwayOfficeExternalInvoke();

    public Page<CoreFileLogBean> selectLogSearchablePagination(Long var1, RestJsonWrapperBean var2);

    public String convertHtmlToPdf(RestJsonWrapperBean var1);

    public void mergePdfList(List<FilePathDTO> var1, FilePathDTO var2);

    public String convertHtmlToWord(RestJsonWrapperBean var1);
}
