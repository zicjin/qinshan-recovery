package com.sunwayworld.emc.module.mdm.equipt.equipt.bean;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.annotation.JSONField;

public class PfEquiptPlanDTO {

    private String equiptNo; // 站内编号

    private String equiptName; // 仪器名称

    private String specification; // 规格型号

    private String verifyOrg;//校检单位

    private String cycle;// 检定周期
    
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate maintLastDate;// 最近检定日期
    
    private String keepUserId; // 保管人
    
    private String keepUserName; // 保管人

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate maintPlanDate;// 计划检定日期
    
    private String remark;// 备注

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

	public String getVerifyOrg() {
		return verifyOrg;
	}

	public void setVerifyOrg(String verifyOrg) {
		this.verifyOrg = verifyOrg;
	}

	public String getCycle() {
		return cycle;
	}

	public void setCycle(String cycle) {
		this.cycle = cycle;
	}

	public String getKeepUserId() {
		return keepUserId;
	}

	public void setKeepUserId(String keepUserId) {
		this.keepUserId = keepUserId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

    public LocalDate getMaintPlanDate() {
        return maintPlanDate;
    }

    public void setMaintPlanDate(LocalDate maintPlanDate) {
        this.maintPlanDate = maintPlanDate;
    }
    
    public LocalDate getMaintLastDate() {
        return maintLastDate;
    }

    public void setMaintLastDate(LocalDate maintLastDate) {
        this.maintLastDate = maintLastDate;
    }

	public String getKeepUserName() {
		return keepUserName;
	}

	public void setKeepUserName(String keepUserName) {
		this.keepUserName = keepUserName;
	}
    

}
