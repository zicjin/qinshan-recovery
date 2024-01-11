package com.sunwayworld.emc.module.mdm.bizmdm.method.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.data.annotation.Clob;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Activatable;

/**
 * 方法表
 *
 * @author yuanhh@sunwayworld.com 2019年9月10日
 */

@Table("T_EMC_METHOD")
public class EmcMethodBean extends AbstractPersistable<Long> implements Activatable<Long> {

    @Transient
    private static final long serialVersionUID = -813604350664643321L;

    @Id
    private Long id;// 主键
    private String monitorCategory;// 监测类别
    @NotNull(defaultValue = EmcConstant.METHOD_RUN_STATUS_WORKING)
    private String runStatus;// 运行状态
    private String methodName;// 方法名称
    private String methodNameEng;// 方法名称（英文）
    private String methodNo;// 方法编号
    private String year;// 年号
    @NotNull(defaultValue = EmcConstant.METHOD_VERSION_FIRST)
    private String version;// 版本
    private Long replaceMethodId;// 替代方法ID
    private Long replacedMethodId;// 被替代方法ID
    private String replaceRemark;// 方法替代备注
    private String methodCategory;// 方法类别
    private String source;// 方法来源
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate effectiveDate;// 实施日期
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expirationDate;// 过期日期
    private String publishOrgName;// 发布单位
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate publishDate;// 发布日期
    private String originalTemplate;// 原始记录模板
    private String replacedContent;// 被替代内容
    private String replaceContent;// 替代内容
    private String remark;//备注
    private Long maxVersionId;// 最大方法版本ID
    private Long maxVersionNo;// 最大方法版本号
    private String testMethodBasis;// 最大方法版本的检测方法及依据
    private Long maxElnTmplId;// 最大方法版本的ELN模板编码
    @Clob
    private String beforeHandle; // 前处理

    @NotNull(defaultValue = Constant.ACTIVATED_STATUS_YES)
    private String activatedFlag;// 启用标志
    private String activatedById;// 启用人编码
    private String activatedByName;// 启用人名称
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime activatedTime;// 启用时间
    private String createdById;// 制单人编码
    private String createdByName;// 制单人名称
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间
    private String createdByOrgId;// 制单人单位编码
    private String createdByOrgName;// 制单人单位名称

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodNameEng() {
        return methodNameEng;
    }

    public void setMethodNameEng(String methodNameEng) {
        this.methodNameEng = methodNameEng;
    }

    public String getMonitorCategory() {
        return monitorCategory;
    }

    public void setMonitorCategory(String monitorCategory) {
        this.monitorCategory = monitorCategory;
    }

    public String getRunStatus() {
        return runStatus;
    }

    public void setRunStatus(String runStatus) {
        this.runStatus = runStatus;
    }

    public String getMethodNo() {
        return methodNo;
    }

    public void setMethodNo(String methodNo) {
        this.methodNo = methodNo;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Long getReplaceMethodId() {
        return replaceMethodId;
    }

    public void setReplaceMethodId(Long replaceMethodId) {
        this.replaceMethodId = replaceMethodId;
    }

    public Long getReplacedMethodId() {
        return replacedMethodId;
    }

    public void setReplacedMethodId(Long replacedMethodId) {
        this.replacedMethodId = replacedMethodId;
    }

    public String getReplaceRemark() {
        return replaceRemark;
    }

    public void setReplaceRemark(String replaceRemark) {
        this.replaceRemark = replaceRemark;
    }

    public String getMethodCategory() {
        return methodCategory;
    }

    public void setMethodCategory(String methodCategory) {
        this.methodCategory = methodCategory;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getPublishOrgName() {
        return publishOrgName;
    }

    public void setPublishOrgName(String publishOrgName) {
        this.publishOrgName = publishOrgName;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

    public String getOriginalTemplate() {
        return originalTemplate;
    }

    public void setOriginalTemplate(String originalTemplate) {
        this.originalTemplate = originalTemplate;
    }

    public String getReplacedContent() {
        return replacedContent;
    }

    public void setReplacedContent(String replacedContent) {
        this.replacedContent = replacedContent;
    }

    public String getReplaceContent() {
        return replaceContent;
    }

    public void setReplaceContent(String replaceContent) {
        this.replaceContent = replaceContent;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getMaxVersionId() {
        return maxVersionId;
    }

    public void setMaxVersionId(Long maxVersionId) {
        this.maxVersionId = maxVersionId;
    }

    public Long getMaxVersionNo() {
        return maxVersionNo;
    }

    public void setMaxVersionNo(Long maxVersionNo) {
        this.maxVersionNo = maxVersionNo;
    }

    public String getTestMethodBasis() {
        return testMethodBasis;
    }

    public void setTestMethodBasis(String testMethodBasis) {
        this.testMethodBasis = testMethodBasis;
    }

    public Long getMaxElnTmplId() {
        return maxElnTmplId;
    }

    public void setMaxElnTmplId(Long maxElnTmplId) {
        this.maxElnTmplId = maxElnTmplId;
    }

    public String getActivatedFlag() {
        return activatedFlag;
    }

    public void setActivatedFlag(String activatedFlag) {
        this.activatedFlag = activatedFlag;
    }

    public String getActivatedById() {
        return activatedById;
    }

    public void setActivatedById(String activatedById) {
        this.activatedById = activatedById;
    }

    public String getActivatedByName() {
        return activatedByName;
    }

    public void setActivatedByName(String activatedByName) {
        this.activatedByName = activatedByName;
    }

    public LocalDateTime getActivatedTime() {
        return activatedTime;
    }

    public void setActivatedTime(LocalDateTime activatedTime) {
        this.activatedTime = activatedTime;
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

    public String getBeforeHandle() {
        return beforeHandle;
    }

    public void setBeforeHandle(String beforeHandle) {
        this.beforeHandle = beforeHandle;
    }
}
