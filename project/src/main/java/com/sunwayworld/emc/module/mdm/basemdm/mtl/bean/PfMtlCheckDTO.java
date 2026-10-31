package com.sunwayworld.emc.module.mdm.basemdm.mtl.bean;


import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.annotation.JSONField;

public class PfMtlCheckDTO {
    private String mtlName; // 材料名称

    private String batchNo; // 批号


    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireDate;// 失效日期
    
    private String isTermofValidity; 

    private String isSuitTemperature; 

    private String isPackagingDamage; 

    private String isSolutionDeposition; 

    private String other;

    private String checkConclusion; 

	public String getMtlName() {
		return mtlName;
	}

	public void setMtlName(String mtlName) {
		this.mtlName = mtlName;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

    public LocalDateTime getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDateTime expireDate) {
        this.expireDate = expireDate;
    }
    
	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}

	public String getIsTermofValidity() {
		return isTermofValidity;
	}

	public void setIsTermofValidity(String isTermofValidity) {
		this.isTermofValidity = isTermofValidity;
	}

	public String getIsSuitTemperature() {
		return isSuitTemperature;
	}

	public void setIsSuitTemperature(String isSuitTemperature) {
		this.isSuitTemperature = isSuitTemperature;
	}

	public String getIsPackagingDamage() {
		return isPackagingDamage;
	}

	public void setIsPackagingDamage(String isPackagingDamage) {
		this.isPackagingDamage = isPackagingDamage;
	}

	public String getIsSolutionDeposition() {
		return isSolutionDeposition;
	}

	public void setIsSolutionDeposition(String isSolutionDeposition) {
		this.isSolutionDeposition = isSolutionDeposition;
	}

	public String getCheckConclusion() {
		return checkConclusion;
	}

	public void setCheckConclusion(String checkConclusion) {
		this.checkConclusion = checkConclusion;
	}

     
    
}
