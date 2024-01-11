package com.sunwayworld.emc.module.mdm.equipt.equipt.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 仪器授权表
 *
 * @author huangc@sunwayworld.com
 * @date 2020-05-21
 */
@Table("T_EMC_EQUIP_CERT")
public class EmcEquipCertBean extends AbstractInsertable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = -5451546483581208905L;

    @Id
    private Long id;// 主键
    private String createdById;// 制单人编码
    private String createdByName;// 制单人名称
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间
    private String createdByOrgId;// 制单人单位编码
    private String createdByOrgName;// 制单人单位名称
    private String equiptId;// 仪器编码
    private String empId;// 人员编码
    private String equiptName;// 仪器名称
    private String userName;// 人员名称
    private String userDept;// 人员科室
    private String userDeptCode;// 人员科室编码
    private String duty;// 职务
    private String mobile;// 手机

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getEquiptId() {
        return equiptId;
    }

    public void setEquiptId(String equiptId) {
        this.equiptId = equiptId;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getEquiptName() {
        return equiptName;
    }

    public void setEquiptName(String equiptName) {
        this.equiptName = equiptName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserDept() {
        return userDept;
    }

    public void setUserDept(String userDept) {
        this.userDept = userDept;
    }

    public String getUserDeptCode() {
        return userDeptCode;
    }

    public void setUserDeptCode(String userDeptCode) {
        this.userDeptCode = userDeptCode;
    }

    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

}
