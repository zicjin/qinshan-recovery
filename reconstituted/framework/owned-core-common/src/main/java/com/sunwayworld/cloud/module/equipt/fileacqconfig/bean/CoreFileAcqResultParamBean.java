/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.fileacqconfig.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_FILE_ACQ_RESULT_PARAM")
public class CoreFileAcqResultParamBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = 7459620531130510663L;
    @Id
    private Long id;
    private Long fileAcqConfigId;
    private String itemName;
    private Long resultColNo;
    private Long sampleNoColNo;
    private Long itemColNo;
    private Long repeatNoColNo;
    private String createdById;
    private String createdByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String createdByOrgId;
    private String createdByOrgName;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getFileAcqConfigId() {
        return this.fileAcqConfigId;
    }

    public void setFileAcqConfigId(Long fileAcqConfigId) {
        this.fileAcqConfigId = fileAcqConfigId;
    }

    public String getItemName() {
        return this.itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Long getResultColNo() {
        return this.resultColNo;
    }

    public void setResultColNo(Long resultColNo) {
        this.resultColNo = resultColNo;
    }

    public Long getSampleNoColNo() {
        return this.sampleNoColNo;
    }

    public void setSampleNoColNo(Long sampleNoColNo) {
        this.sampleNoColNo = sampleNoColNo;
    }

    public Long getItemColNo() {
        return this.itemColNo;
    }

    public void setItemColNo(Long itemColNo) {
        this.itemColNo = itemColNo;
    }

    public Long getRepeatNoColNo() {
        return this.repeatNoColNo;
    }

    public void setRepeatNoColNo(Long repeatNoColNo) {
        this.repeatNoColNo = repeatNoColNo;
    }

    @Override
    public String getCreatedById() {
        return this.createdById;
    }

    @Override
    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    @Override
    public String getCreatedByName() {
        return this.createdByName;
    }

    @Override
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    @Override
    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    @Override
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String getCreatedByOrgId() {
        return this.createdByOrgId;
    }

    @Override
    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    @Override
    public String getCreatedByOrgName() {
        return this.createdByOrgName;
    }

    @Override
    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }
}
