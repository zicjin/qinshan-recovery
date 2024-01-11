package com.sunwayworld.emc.module.mdm.equipt.equipt.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.Insertable;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * 仪器维护维修
 * 
 * @author licm@sunwayworld.com
 * @date 2022-09-13
 */
@Table("T_EMC_EQUIPT_MAINTAIN_REPAIR")
public class EmcEquiptMaintainRepairBean extends AbstractInsertable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = -82566196618711043L;

    @Id
    private Long id;// 主键
    private Long equiptId;// 仪器id
    private LocalDate maintainRepairDate;// 维护维修日期
    private String maintainRepairConcent;// 维护维修内容
    private String maintainRepairSign;// 签名
    private String equiptFailure;// 仪器故障说明
    private String maintainrepairunit;// 维修单位(人)
    private String planCategory;// 计划类型
    private String createdById;// 制单人编码
    private String createdByName;// 制单人名称
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间
    private String createdByOrgId;// 制单人单位编码
    private String createdByOrgName;// 制单人单位名称

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

    public LocalDate getMaintainRepairDate() {
        return maintainRepairDate;
    }

    public void setMaintainRepairDate(LocalDate maintainRepairDate) {
        this.maintainRepairDate = maintainRepairDate;
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

    public String getEquiptFailure() {
        return equiptFailure;
    }

    public void setEquiptFailure(String equiptFailure) {
        this.equiptFailure = equiptFailure;
    }

    public String getMaintainrepairunit() {
        return maintainrepairunit;
    }

    public void setMaintainrepairunit(String maintainrepairunit) {
        this.maintainrepairunit = maintainrepairunit;
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

	public String getPlanCategory() {
		return planCategory;
	}

	public void setPlanCategory(String planCategory) {
		this.planCategory = planCategory;
	}

}
