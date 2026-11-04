/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Auditable;
import com.sunwayworld.framework.support.domain.Insertable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_LES_TEMPLATE")
public class CoreLesTemplateBean
extends AbstractInsertable<Long>
implements Insertable<Long>,
Auditable<Long> {
    @Transient
    private static final long serialVersionUID = 2447880144792286433L;
    @Id
    private Long id;
    private Long baseId;
    private String templateNo;
    private String templateName;
    @NotNull(defaultValue="LES")
    private String lesCategory;
    private String lesTemplateCategory;
    @NotNull(defaultValue="1")
    private Long templateVersion;
    private String locked;
    private String remark;
    @NotNull(defaultValue="draft")
    private String templateStatus;
    @NotNull(defaultValue="draft")
    private String processStatus;
    private String approvedById;
    private String approvedByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approveDate;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getBaseId() {
        return this.baseId;
    }

    public void setBaseId(Long baseId) {
        this.baseId = baseId;
    }

    public String getTemplateNo() {
        return this.templateNo;
    }

    public void setTemplateNo(String templateNo) {
        this.templateNo = templateNo;
    }

    public String getTemplateName() {
        return this.templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getLesCategory() {
        return this.lesCategory;
    }

    public void setLesCategory(String lesCategory) {
        this.lesCategory = lesCategory;
    }

    public String getLesTemplateCategory() {
        return this.lesTemplateCategory;
    }

    public void setLesTemplateCategory(String lesTemplateCategory) {
        this.lesTemplateCategory = lesTemplateCategory;
    }

    public Long getTemplateVersion() {
        return this.templateVersion;
    }

    public void setTemplateVersion(Long templateVersion) {
        this.templateVersion = templateVersion;
    }

    public String getLocked() {
        return this.locked;
    }

    public void setLocked(String locked) {
        this.locked = locked;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTemplateStatus() {
        return this.templateStatus;
    }

    public void setTemplateStatus(String templateStatus) {
        this.templateStatus = templateStatus;
    }

    @Override
    public String getProcessStatus() {
        return this.processStatus;
    }

    @Override
    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    public String getApprovedById() {
        return this.approvedById;
    }

    public void setApprovedById(String approvedById) {
        this.approvedById = approvedById;
    }

    public String getApprovedByName() {
        return this.approvedByName;
    }

    public void setApprovedByName(String approvedByName) {
        this.approvedByName = approvedByName;
    }

    public LocalDateTime getApproveDate() {
        return this.approveDate;
    }

    public void setApproveDate(LocalDateTime approveDate) {
        this.approveDate = approveDate;
    }
}
