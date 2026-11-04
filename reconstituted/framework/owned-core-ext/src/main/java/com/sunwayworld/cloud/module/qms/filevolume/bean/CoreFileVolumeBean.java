/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.filevolume.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Activatable;
import com.sunwayworld.framework.support.domain.Insertable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_FILE_VOLUME")
public class CoreFileVolumeBean
extends AbstractInsertable<Long>
implements Insertable<Long>,
Activatable<Long> {
    @Transient
    private static final long serialVersionUID = -82566196618711043L;
    @Id
    private Long id;
    private Long sortor;
    private String fileVolumeNo;
    private String fileVolumeName;
    private String serialNumber;
    @NotNull(defaultValue="1")
    private String activatedFlag;
    private String activatedById;
    private String activatedByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime activatedTime;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deactivatedTime;
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

    public String getFileVolumeNo() {
        return this.fileVolumeNo;
    }

    public void setFileVolumeNo(String fileVolumeNo) {
        this.fileVolumeNo = fileVolumeNo;
    }

    public String getFileVolumeName() {
        return this.fileVolumeName;
    }

    public void setFileVolumeName(String fileVolumeName) {
        this.fileVolumeName = fileVolumeName;
    }

    public String getSerialNumber() {
        return this.serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    @Override
    public String getActivatedFlag() {
        return this.activatedFlag;
    }

    @Override
    public void setActivatedFlag(String activatedFlag) {
        this.activatedFlag = activatedFlag;
    }

    @Override
    public String getActivatedById() {
        return this.activatedById;
    }

    @Override
    public void setActivatedById(String activatedById) {
        this.activatedById = activatedById;
    }

    @Override
    public String getActivatedByName() {
        return this.activatedByName;
    }

    @Override
    public void setActivatedByName(String activatedByName) {
        this.activatedByName = activatedByName;
    }

    @Override
    public LocalDateTime getActivatedTime() {
        return this.activatedTime;
    }

    @Override
    public void setActivatedTime(LocalDateTime activatedTime) {
        this.activatedTime = activatedTime;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getDeactivatedTime() {
        return this.deactivatedTime;
    }

    public void setDeactivatedTime(LocalDateTime deactivatedTime) {
        this.deactivatedTime = deactivatedTime;
    }
}
