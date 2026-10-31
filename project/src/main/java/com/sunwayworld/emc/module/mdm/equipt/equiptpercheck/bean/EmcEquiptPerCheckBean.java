package com.sunwayworld.emc.module.mdm.equipt.equiptpercheck.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.framework.data.annotation.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractAuditable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 期间核查表
 *
 * @author chend@sunwayworld.com
 * @date 2020-09-03
 */
@Table("T_EMC_EQUIPT_PER_CHECK")
public class EmcEquiptPerCheckBean extends AbstractAuditable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = -82566196618711043L;

    @Id
    private Long id;// 仪器期间核查记录id
    private Long equiptId;// 仪器Id
    private String equiptNo;// 站内编号
    private String equiptName;// 仪器名称
    private String specification;// 规格型号
    private String checkPerson;//审核人员
    private String cycle;// 检定周期
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime checkTime;// 最近检定时间
    private String equiptment;// 检查所用仪器设备
    private String checkReason;// 核查依据
    private String checkWay;// 核查方式
    private String checkProcess;// 核查过程记录
    private String checkResult;// 核查结果
    private String checkConclusion;// 核查结论
    private String opinion;// 处理意见
    private String recorder;// 记录人
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime recorderDate;// 记录日期
    private String director;// 技术主管
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime directorDate;// 技术主管记录日期
    private String principal;// 技术负责人
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime principalDate;// 技术负责人记录日期
    private String createdById;// 制单人编码
    private String createdByName;// 制单人名称
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间
    private String createdByOrgId;// 制单人单位编码
    private String createdByOrgName;// 制单人单位名称
    @NotNull(defaultValue = EmcBizConstant.PROJECT_PROCESSSTATUS_DRAFT)
    private String processStatus;// 状态:(draft、approve、done)
    private String remark;//备注

    public String getCheckPerson() {
        return checkPerson;
    }

    public void setCheckPerson(String checkPerson) {
        this.checkPerson = checkPerson;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEquiptId() {
        return equiptId;
    }

    public void setEquiptId(Long equiptId) {
        this.equiptId = equiptId;
    }

    public String getEquiptNo() {
        return equiptNo;
    }

    public void setEquiptNo(String equiptNo) {
        this.equiptNo = equiptNo;
    }

    public String getEquiptName() {
        return equiptName;
    }

    public void setEquiptName(String equiptName) {
        this.equiptName = equiptName;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

    public LocalDateTime getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(LocalDateTime checkTime) {
        this.checkTime = checkTime;
    }

    public String getEquiptment() {
        return equiptment;
    }

    public void setEquiptment(String equiptment) {
        this.equiptment = equiptment;
    }

    public String getCheckReason() {
        return checkReason;
    }

    public void setCheckReason(String checkReason) {
        this.checkReason = checkReason;
    }

    public String getCheckWay() {
        return checkWay;
    }

    public void setCheckWay(String checkWay) {
        this.checkWay = checkWay;
    }

    public String getCheckProcess() {
        return checkProcess;
    }

    public void setCheckProcess(String checkProcess) {
        this.checkProcess = checkProcess;
    }

    public String getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(String checkResult) {
        this.checkResult = checkResult;
    }

    public String getCheckConclusion() {
        return checkConclusion;
    }

    public void setCheckConclusion(String checkConclusion) {
        this.checkConclusion = checkConclusion;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public String getRecorder() {
        return recorder;
    }

    public void setRecorder(String recorder) {
        this.recorder = recorder;
    }

    public LocalDateTime getRecorderDate() {
        return recorderDate;
    }

    public void setRecorderDate(LocalDateTime recorderDate) {
        this.recorderDate = recorderDate;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public LocalDateTime getDirectorDate() {
        return directorDate;
    }

    public void setDirectorDate(LocalDateTime directorDate) {
        this.directorDate = directorDate;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public LocalDateTime getPrincipalDate() {
        return principalDate;
    }

    public void setPrincipalDate(LocalDateTime principalDate) {
        this.principalDate = principalDate;
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

    @Override
    public String getProcessStatus() {
        return processStatus;
    }

    @Override
    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
