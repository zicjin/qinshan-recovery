package com.sunwayworld.emc.module.mdm.equipt.equipt.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.annotation.JSONField;

public class PfEquiptPerCheckDTO {

    private String equiptNo; // 站内编号

    private String equiptName; // 仪器名称

    private String specification; // 规格型号
    
    
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate maintLastDate;// 最近检定日期
    
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime maintDate;// 核查日期
    
    //核查指标
    private String checkIndex;//核查指标
    
    private String checkWay; // 核查方法

    private String checkReason;// 核查依据

    private String checkUserId; // 核查人ID

    private String checkUserName; // 核查人

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

	public String getCheckIndex() {
		return checkIndex;
	}

	public void setCheckIndex(String checkIndex) {
		this.checkIndex = checkIndex;
	}

	public String getCheckWay() {
		return checkWay;
	}

	public void setCheckWay(String checkWay) {
		this.checkWay = checkWay;
	}

	public String getCheckReason() {
		return checkReason;
	}

	public void setCheckReason(String checkReason) {
		this.checkReason = checkReason;
	}

	public String getCheckUserId() {
		return checkUserId;
	}

	public void setCheckUserId(String checkUserId) {
		this.checkUserId = checkUserId;
	}

	public String getCheckUserName() {
		return checkUserName;
	}

	public void setCheckUserName(String checkUserName) {
		this.checkUserName = checkUserName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

    public LocalDateTime getMaintDate() {
        return maintDate;
    }

    public void setMaintDate(LocalDateTime maintDate) {
        this.maintDate = maintDate;
    }
    
    public LocalDate getMaintLastDate() {
        return maintLastDate;
    }

    public void setMaintLastDate(LocalDate maintLastDate) {
        this.maintLastDate = maintLastDate;
    }
    
}
