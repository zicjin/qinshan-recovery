package com.sunwayworld.emc.module.mdm.purchapply.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 采购明细
 * @author zhanghy@sunwayworld.com 2020-09-03
 */
@Table("T_DINGAN_PURCH_DETAIL")
public class EmcPurchDetailBean extends AbstractInsertable<Long> implements Insertable<Long> {

	private static final long serialVersionUID = 7386646132517831647L;

	private Long id; // 主键

	private Long purchId; // 采购单ID

	private String purchaseName; // 采购品名称

	private String specification; // 规格

	private String technicalIndex; // 技术指标及要求

	private String supplier; // 供应商

	private Long numb; // 数量

	private String remark; // 备注

    private String createdById;// 制单人编码

    private String createdByName;// 制单人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPurchId() {
		return purchId;
	}

	public void setPurchId(Long purchId) {
		this.purchId = purchId;
	}

	public String getPurchaseName() {
		return purchaseName;
	}

	public void setPurchaseName(String purchaseName) {
		this.purchaseName = purchaseName;
	}

	public String getSpecification() {
		return specification;
	}

	public void setSpecification(String specification) {
		this.specification = specification;
	}

	public String getTechnicalIndex() {
		return technicalIndex;
	}

	public void setTechnicalIndex(String technicalIndex) {
		this.technicalIndex = technicalIndex;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public Long getNumb() {
		return numb;
	}

	public void setNumb(Long numb) {
		this.numb = numb;
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

}
