package com.sunwayworld.emc.module.mdm.equipt.equipt.bean;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.annotation.JSONField;

public class PfEquiptMaintainRepairDTO {
	
    private String equiptNo; // 站内编号
     
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate maintainRepairDate;//维护日期
    
    private String maintainRepairConcent;//维护、维修内容
    
    private String maintainRepairSign;//签名

    private String equiptFailure;//故障说明
    
    private String maintainrepairunit;//维修单位

	public String getEquiptNo() {
		return equiptNo;
	}

	public void setEquiptNo(String equiptNo) {
		this.equiptNo = equiptNo;
	}

	public String getMaintainRepairConcent() {
		return maintainRepairConcent;
	}

	public void setMaintainRepairConcent(String maintainRepairConcent) {
		this.maintainRepairConcent = maintainRepairConcent;
	}

	public String getMaintainRepairSign() {
		return maintainRepairSign;
	}

	public void setMaintainRepairSign(String maintainRepairSign) {
		this.maintainRepairSign = maintainRepairSign;
	}

	public String getMaintainrepairunit() {
		return maintainrepairunit;
	}

	public void setMaintainrepairunit(String maintainrepairunit) {
		this.maintainrepairunit = maintainrepairunit;
	}

	public String getEquiptFailure() {
		return equiptFailure;
	}

	public void setEquiptFailure(String equiptFailure) {
		this.equiptFailure = equiptFailure;
	}
	
    public LocalDate getMaintainRepairDate() {
        return maintainRepairDate;
    }

    public void setMaintainRepairDate(LocalDate maintainRepairDate) {
        this.maintainRepairDate = maintainRepairDate;
    }
}
