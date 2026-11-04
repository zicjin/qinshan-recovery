/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.filemanage.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractAuditable;
import com.sunwayworld.framework.support.domain.Auditable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_FILE_MANAGE_VERSION")
public class CoreFileManageVersionBean
extends AbstractAuditable<Long>
implements Auditable<Long> {
    @Transient
    private static final long serialVersionUID = -82566196618711043L;
    @Id
    private Long id;
    private Long fileManageId;
    private String versionState;
    private Long versionNo;
    private Long refFileId;
    private String refFileName;
    private Long auditState;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate draftDate;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate effectiveDate;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate expiryDate;
    private String fileNo;
    private String auditNo;
    private String editUserId;
    private String editUserName;
    private String checkUserId;
    private String checkUserName;
    private String approveUserId;
    private String approveUserName;
    private String receiveOrgId;
    private String receiveOrgName;
    private String archiveOrgId;
    private String archiveOrgName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate releaseDate;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate carryDate;
    private String createdById;
    private String createdByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String createdByOrgId;
    private String createdByOrgName;
    private String remark;
    private String filePath;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getFileManageId() {
        return this.fileManageId;
    }

    public void setFileManageId(Long fileManageId) {
        this.fileManageId = fileManageId;
    }

    public String getVersionState() {
        return this.versionState;
    }

    public void setVersionState(String versionState) {
        this.versionState = versionState;
    }

    public Long getVersionNo() {
        return this.versionNo;
    }

    public void setVersionNo(Long versionNo) {
        this.versionNo = versionNo;
    }

    public Long getRefFileId() {
        return this.refFileId;
    }

    public void setRefFileId(Long refFileId) {
        this.refFileId = refFileId;
    }

    public String getRefFileName() {
        return this.refFileName;
    }

    public void setRefFileName(String refFileName) {
        this.refFileName = refFileName;
    }

    public Long getAuditState() {
        return this.auditState;
    }

    public void setAuditState(Long auditState) {
        this.auditState = auditState;
    }

    public LocalDate getDraftDate() {
        return this.draftDate;
    }

    public void setDraftDate(LocalDate draftDate) {
        this.draftDate = draftDate;
    }

    public LocalDate getEffectiveDate() {
        return this.effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public LocalDate getExpiryDate() {
        return this.expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getFileNo() {
        return this.fileNo;
    }

    public void setFileNo(String fileNo) {
        this.fileNo = fileNo;
    }

    public String getAuditNo() {
        return this.auditNo;
    }

    public void setAuditNo(String auditNo) {
        this.auditNo = auditNo;
    }

    public String getCheckUserId() {
        return this.checkUserId;
    }

    public void setCheckUserId(String checkUserId) {
        this.checkUserId = checkUserId;
    }

    public String getCheckUserName() {
        return this.checkUserName;
    }

    public void setCheckUserName(String checkUserName) {
        this.checkUserName = checkUserName;
    }

    public String getApproveUserId() {
        return this.approveUserId;
    }

    public void setApproveUserId(String approveUserId) {
        this.approveUserId = approveUserId;
    }

    public String getApproveUserName() {
        return this.approveUserName;
    }

    public void setApproveUserName(String approveUserName) {
        this.approveUserName = approveUserName;
    }

    public String getReceiveOrgId() {
        return this.receiveOrgId;
    }

    public void setReceiveOrgId(String receiveOrgId) {
        this.receiveOrgId = receiveOrgId;
    }

    public String getReceiveOrgName() {
        return this.receiveOrgName;
    }

    public void setReceiveOrgName(String receiveOrgName) {
        this.receiveOrgName = receiveOrgName;
    }

    public String getArchiveOrgId() {
        return this.archiveOrgId;
    }

    public void setArchiveOrgId(String archiveOrgId) {
        this.archiveOrgId = archiveOrgId;
    }

    public String getArchiveOrgName() {
        return this.archiveOrgName;
    }

    public void setArchiveOrgName(String archiveOrgName) {
        this.archiveOrgName = archiveOrgName;
    }

    public LocalDate getReleaseDate() {
        return this.releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public LocalDate getCarryDate() {
        return this.carryDate;
    }

    public void setCarryDate(LocalDate carryDate) {
        this.carryDate = carryDate;
    }

    public String getCreatedById() {
        return this.createdById;
    }

    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    public String getCreatedByName() {
        return this.createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getCreatedByOrgId() {
        return this.createdByOrgId;
    }

    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    public String getCreatedByOrgName() {
        return this.createdByOrgName;
    }

    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getEditUserId() {
        return this.editUserId;
    }

    public void setEditUserId(String editUserId) {
        this.editUserId = editUserId;
    }

    public String getEditUserName() {
        return this.editUserName;
    }

    public void setEditUserName(String editUserName) {
        this.editUserName = editUserName;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
