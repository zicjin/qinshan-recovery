/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.filemanage.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

@Table(value="T_CORE_FILE_MANAGE")
public class CoreFileManageBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = -82566196618711043L;
    @Id
    private Long id;
    private Long sortor;
    private Long fileTypeId;
    private String fileTypeName;
    private Long fileCategoryId;
    private String fileCategoryName;
    private String fileNo;
    private String fileName;
    private String auditNo;
    private String fileVolumeId;
    private String fileVolumeName;
    private String remark;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getSortor() {
        return this.sortor;
    }

    public void setSortor(Long sortor) {
        this.sortor = sortor;
    }

    public Long getFileTypeId() {
        return this.fileTypeId;
    }

    public void setFileTypeId(Long fileTypeId) {
        this.fileTypeId = fileTypeId;
    }

    public String getFileTypeName() {
        return this.fileTypeName;
    }

    public void setFileTypeName(String fileTypeName) {
        this.fileTypeName = fileTypeName;
    }

    public Long getFileCategoryId() {
        return this.fileCategoryId;
    }

    public void setFileCategoryId(Long fileCategoryId) {
        this.fileCategoryId = fileCategoryId;
    }

    public String getFileCategoryName() {
        return this.fileCategoryName;
    }

    public void setFileCategoryName(String fileCategoryName) {
        this.fileCategoryName = fileCategoryName;
    }

    public String getFileNo() {
        return this.fileNo;
    }

    public void setFileNo(String fileNo) {
        this.fileNo = fileNo;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getAuditNo() {
        return this.auditNo;
    }

    public void setAuditNo(String auditNo) {
        this.auditNo = auditNo;
    }

    public String getFileVolumeId() {
        return this.fileVolumeId;
    }

    public void setFileVolumeId(String fileVolumeId) {
        this.fileVolumeId = fileVolumeId;
    }

    public String getFileVolumeName() {
        return this.fileVolumeName;
    }

    public void setFileVolumeName(String fileVolumeName) {
        this.fileVolumeName = fileVolumeName;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
