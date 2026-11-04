/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.qualitycontrol.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_QUALITY_CONTROL_ITEM")
public class CoreQualityControlItemBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = -82566196618711043L;
    @Id
    private Long id;
    private String createdById;
    private String createdByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String createdByOrgId;
    private String createdByOrgName;
    private Long auditId;
    private Long orderNo;
    private String folderNo;
    private String matName;
    private String testParam;
    private String standardMtrl;
    private String testStandard;
    private String equNo;
    private String testUserId;
    private String testUserName;
    private String testReq;
    private String testResult;
    private String remark;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
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

    public Long getAuditId() {
        return this.auditId;
    }

    public void setAuditId(Long auditId) {
        this.auditId = auditId;
    }

    public Long getOrderNo() {
        return this.orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public String getFolderNo() {
        return this.folderNo;
    }

    public void setFolderNo(String folderNo) {
        this.folderNo = folderNo;
    }

    public String getMatName() {
        return this.matName;
    }

    public void setMatName(String matName) {
        this.matName = matName;
    }

    public String getTestParam() {
        return this.testParam;
    }

    public void setTestParam(String testParam) {
        this.testParam = testParam;
    }

    public String getStandardMtrl() {
        return this.standardMtrl;
    }

    public void setStandardMtrl(String standardMtrl) {
        this.standardMtrl = standardMtrl;
    }

    public String getTestStandard() {
        return this.testStandard;
    }

    public void setTestStandard(String testStandard) {
        this.testStandard = testStandard;
    }

    public String getEquNo() {
        return this.equNo;
    }

    public void setEquNo(String equNo) {
        this.equNo = equNo;
    }

    public String getTestUserId() {
        return this.testUserId;
    }

    public void setTestUserId(String testUserId) {
        this.testUserId = testUserId;
    }

    public String getTestUserName() {
        return this.testUserName;
    }

    public void setTestUserName(String testUserName) {
        this.testUserName = testUserName;
    }

    public String getTestReq() {
        return this.testReq;
    }

    public void setTestReq(String testReq) {
        this.testReq = testReq;
    }

    public String getTestResult() {
        return this.testResult;
    }

    public void setTestResult(String testResult) {
        this.testResult = testResult;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
