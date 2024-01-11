package com.sunwayworld.emc.module.mdm.bizmdm.samplenumber.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.Insertable;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * 采样单编码信息
 *
 * @author zhangyl@sunwayworld.com@sunwayworld.com
 * @date 2021-06-08
 */
@Table("T_EMC_SAMPLENUMBER")
public class EmcSamplenumberBean extends AbstractInsertable<Long> implements Insertable<Long> {

	@Transient
	private static final long serialVersionUID = -4838015098633741685L;

    @Id
    private Long id;// 主键
    private String orgid;// 所属单位ID
    private Long reportconfiglineid;// 报表ID
    private String numbercode;// 采样单编码
    private String effectiveDate;// 生效日期
    private String createdById;// 制单人编码
    private String createdByName;// 制单人名称
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间
    private String createdByOrgId;// 制单人单位编码
    private String createdByOrgName;// 制单人单位名称

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getOrgid() {
        return orgid;
    }

    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }

    public Long getReportconfiglineid() {
        return reportconfiglineid;
    }

    public void setReportconfiglineid(Long reportconfiglineid) {
        this.reportconfiglineid = reportconfiglineid;
    }

    public String getNumbercode() {
        return numbercode;
    }

    public void setNumbercode(String numbercode) {
        this.numbercode = numbercode;
    }

    @Override
    public String getCreatedById() {
        return createdById;
    }

    @Override
    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    @Override
    public String getCreatedByName() {
        return createdByName;
    }

    @Override
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    @Override
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    @Override
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String getCreatedByOrgId() {
        return createdByOrgId;
    }

    @Override
    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    @Override
    public String getCreatedByOrgName() {
        return createdByOrgName;
    }

    @Override
    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

}
