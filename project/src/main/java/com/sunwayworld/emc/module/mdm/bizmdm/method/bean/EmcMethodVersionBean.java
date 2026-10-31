package com.sunwayworld.emc.module.mdm.bizmdm.method.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 方法版本表
 *
 * @author yuanhh@sunwayworld.com 2019年9月10日
 */

@Table("T_EMC_METHOD_VERSION")
public class EmcMethodVersionBean extends AbstractPersistable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = 8395909410805194798L;

    @Id
    private Long id;// 主键
    private Long methodId;// 方法编码
    private Long versionNo;// 版本号
    private String testMethodBasis;// 检测方法及依据
    private Long elnTmplId;// ELN模板编码
    private Long reportConfigLineId;// 原始记录模板
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime beginDate;// 开始日期
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime expireDate;// 过期日期
    private String createdById;// 制单人编码
    private String createdByName;// 制单人名称
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间
    private String createdByOrgId;// 制单人单位编码
    private String createdByOrgName;// 制单人单位名称

    /**
     * 江苏新锐
     */
    private String trialScope;// 试用范围

    /**
     * 上海水文
     */
    private String standardNo;//标准编号

    private String methodShortName;//方法简称


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMethodId() {
        return methodId;
    }

    public void setMethodId(Long methodId) {
        this.methodId = methodId;
    }

    public Long getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(Long versionNo) {
        this.versionNo = versionNo;
    }

    public String getTestMethodBasis() {
        return testMethodBasis;
    }

    public void setTestMethodBasis(String testMethodBasis) {
        this.testMethodBasis = testMethodBasis;
    }

    public LocalDateTime getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(LocalDateTime beginDate) {
        this.beginDate = beginDate;
    }

    public LocalDateTime getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDateTime expireDate) {
        this.expireDate = expireDate;
    }

    public Long getElnTmplId() {
        return elnTmplId;
    }

    public void setElnTmplId(Long elnTmplId) {
        this.elnTmplId = elnTmplId;
    }

    public String getCreatedById() {
        return createdById;
    }

    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getCreatedByOrgId() {
        return createdByOrgId;
    }

    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    public String getCreatedByOrgName() {
        return createdByOrgName;
    }

    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }

    public Long getReportConfigLineId() {
        return reportConfigLineId;
    }

    public void setReportConfigLineId(Long reportConfigLineId) {
        this.reportConfigLineId = reportConfigLineId;
    }

    public String getTrialScope() {
        return trialScope;
    }

    public void setTrialScope(String trialScope) {
        this.trialScope = trialScope;
    }

    public String getStandardNo() {
        return standardNo;
    }

    public void setStandardNo(String standardNo) {
        this.standardNo = standardNo;
    }

    public String getMethodShortName() {
        return methodShortName;
    }

    public void setMethodShortName(String methodShortName) {
        this.methodShortName = methodShortName;
    }
}
