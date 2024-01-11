package com.sunwayworld.emc.module.mdm.basemdm.qccategory.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * QC类型
 *
 * @author zhangsy@sunwayworld.com 2019/9/4
 *
 */
@Table("T_EMC_QC_CATEGORY")
public class EmcQcCategoryBean extends AbstractInsertable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = -6058219026762440216L;

    @Id
    private Long id;

    private String qcCategoryId; // QC类型编码

    private String qcName; // QC类型中文

    private Long qcGroup; // QC组

    private String parentId; // 上级QC类型编码

    private String qcShortHand; // QC简码

    private String updateParNo; // 更新父样编号

    private String useCategory; // 质控使用类型

    private String secrecy; // 是否保密

    private String parOrder; // 是否有父样

    private String addQc; // 能否添加质控

    private String addFolder; // 是否添加点位

    private Long addNum; // 添加qc数量

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

    public String getQcCategoryId() {
        return qcCategoryId;
    }

    public void setQcCategoryId(String qcCategoryId) {
        this.qcCategoryId = qcCategoryId;
    }

    public Long getQcGroup() {
        return qcGroup;
    }

    public void setQcGroup(Long qcGroup) {
        this.qcGroup = qcGroup;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getQcShortHand() {
        return qcShortHand;
    }

    public void setQcShortHand(String qcShortHand) {
        this.qcShortHand = qcShortHand;
    }

    public String getUpdateParNo() {
        return updateParNo;
    }

    public void setUpdateParNo(String updateParNo) {
        this.updateParNo = updateParNo;
    }

    public String getUseCategory() {
        return useCategory;
    }

    public void setUseCategory(String useCategory) {
        this.useCategory = useCategory;
    }

    public String getSecrecy() {
        return secrecy;
    }

    public void setSecrecy(String secrecy) {
        this.secrecy = secrecy;
    }

    public String getParOrder() {
        return parOrder;
    }

    public void setParOrder(String parOrder) {
        this.parOrder = parOrder;
    }

    public String getAddQc() {
        return addQc;
    }

    public void setAddQc(String addQc) {
        this.addQc = addQc;
    }

    public Long getAddNum() {
        return addNum;
    }

    public void setAddNum(Long addNum) {
        this.addNum = addNum;
    }

    public String getQcName() {
        return qcName;
    }

    public void setQcName(String qcName) {
        this.qcName = qcName;
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

	public String getAddFolder() {
		return addFolder;
	}

	public void setAddFolder(String addFolder) {
		this.addFolder = addFolder;
	}

}
