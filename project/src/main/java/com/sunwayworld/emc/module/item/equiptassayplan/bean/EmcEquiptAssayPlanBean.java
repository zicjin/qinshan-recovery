package com.sunwayworld.emc.module.item.equiptassayplan.bean;

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
 * 仪器检定校准计划
 * 
 * @author licm@sunwayworld.com
 * @date 2022-09-27
 */
@Table("T_EMC_EQUIPT_ASSAY_PLAN")
public class EmcEquiptAssayPlanBean extends AbstractInsertable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = -82566196618711043L;

    @Id
    private Long id;// 主键
    private String codename;// 维护事件
    private String verifyOrg;// 校检单位
    private String cycle;// 校检周期
    private LocalDate maintDate;// 校检日期
    private LocalDate maintEndDate;// 有效期至
    private String warningDays;// 提前提醒(天)
    private String gist;// 校检依据
    private String certCategory;// 证书类型
    private String remark;// 备注
    private String plancategory;// 计划种类
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

    public String getCodename() {
        return codename;
    }

    public void setCodename(String codename) {
        this.codename = codename;
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

    public LocalDate getMaintDate() {
        return maintDate;
    }

    public void setMaintDate(LocalDate maintDate) {
        this.maintDate = maintDate;
    }

    public LocalDate getMaintEndDate() {
        return maintEndDate;
    }

    public void setMaintEndDate(LocalDate maintEndDate) {
        this.maintEndDate = maintEndDate;
    }

    public String getWarningDays() {
        return warningDays;
    }

    public void setWarningDays(String warningDays) {
        this.warningDays = warningDays;
    }

    public String getGist() {
        return gist;
    }

    public void setGist(String gist) {
        this.gist = gist;
    }

    public String getCertCategory() {
        return certCategory;
    }

    public void setCertCategory(String certCategory) {
        this.certCategory = certCategory;
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

	public String getPlancategory() {
		return plancategory;
	}

	public void setPlancategory(String plancategory) {
		this.plancategory = plancategory;
	}

}
