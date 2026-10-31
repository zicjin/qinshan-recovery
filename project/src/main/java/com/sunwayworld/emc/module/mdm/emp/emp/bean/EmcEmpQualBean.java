package com.sunwayworld.emc.module.mdm.emp.emp.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 人员-资格证书
 *
 * @author yuanhh@sunwayworld.com 2019年9月4日
 */
@Table("T_EMC_EMP_QUAL")
public class EmcEmpQualBean extends AbstractInsertable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = -5779939892598083675L;

    @Id
    private Long id; // 主键
    private String empId; // 用户名
    private String qualNo; // 证件编号
    private String qualName; // 证件名称/号
    private String qualOrg; // 发证单位
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate validDate; // 有效期

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate authDate; // 发证日期

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expireDate; // 到期日期

    private Long warningDays; // 预警天数

    private String createdById; // 制单人编码
    private String createdByName; // 制单人名称
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime; // 制单时间
    private String createdByOrgId; // 制单人单位编码
    private String createdByOrgName; // 制单人单位名称

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getEmpId() {
        return empId;
    }
    public void setEmpId(String empId) {
        this.empId = empId;
    }
    public String getQualName() {
        return qualName;
    }
    public void setQualName(String qualName) {
        this.qualName = qualName;
    }
    public String getQualOrg() {
        return qualOrg;
    }
    public void setQualOrg(String qualOrg) {
        this.qualOrg = qualOrg;
    }
    public LocalDate getValidDate() {
        return validDate;
    }
    public void setValidDate(LocalDate validDate) {
        this.validDate = validDate;
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

    public String getQualNo() {
        return qualNo;
    }

    public void setQualNo(String qualNo) {
        this.qualNo = qualNo;
    }

    public LocalDate getAuthDate() {
        return authDate;
    }

    public void setAuthDate(LocalDate authDate) {
        this.authDate = authDate;
    }

    public LocalDate getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

    public Long getWarningDays() {
        return warningDays;
    }

    public void setWarningDays(Long warningDays) {
        this.warningDays = warningDays;
    }
}
