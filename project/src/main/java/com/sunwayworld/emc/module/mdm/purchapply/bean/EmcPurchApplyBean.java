package com.sunwayworld.emc.module.mdm.purchapply.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractAuditable;
import com.sunwayworld.framework.support.domain.Auditable;

/**
 * 采购申请
 * @author zhanghy@sunwayworld.com 2020-09-03
 */
@Table("T_DINGAN_PURCH")
public class EmcPurchApplyBean extends AbstractAuditable<Long> implements Auditable<Long> {

	private static final long serialVersionUID = -3923893261681386712L;

	private Long id; // 主键

	private String purchaseCategory; // 采购品类别

	private String instruction; // 说明

	private String remark; // 备注

    private String createdById;// 制单人编码

    private String createdByName;// 制单人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间

	private String auditorId;//审核人编码

	private String auditorName;//审核人名称

	private String confirmerId;//确认人编码

	private String confirmerName;//确认人名称

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPurchaseCategory() {
		return purchaseCategory;
	}

	public void setPurchaseCategory(String purchaseCategory) {
		this.purchaseCategory = purchaseCategory;
	}

	public String getInstruction() {
		return instruction;
	}

	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	public String getAuditorId() {
		return auditorId;
	}

	public void setAuditorId(String auditorId) {
		this.auditorId = auditorId;
	}

	public String getAuditorName() {
		return auditorName;
	}

	public void setAuditorName(String auditorName) {
		this.auditorName = auditorName;
	}

	public String getConfirmerId() {
		return confirmerId;
	}

	public void setConfirmerId(String confirmerId) {
		this.confirmerId = confirmerId;
	}

	public String getConfirmerName() {
		return confirmerName;
	}

	public void setConfirmerName(String confirmerName) {
		this.confirmerName = confirmerName;
	}
}
