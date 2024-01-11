package com.sunwayworld.emc.module.bp.voc.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * voc瓶
 *
 * @author licm@sunwayworld.com
 * @date 2021-09-22
 */
@Table("T_EMC_VOC_WEIGHTING")
public class PfVocWeightingBean extends AbstractInsertable<Long> implements Insertable<Long> {

    @Transient
	private static final long serialVersionUID = 7628315254023972090L;

    @Id
    private Long id;// 主键Id
    private String vocNumber;// VOC瓶号
    private String weighingUserName;// 称重人
    private String emptyBottleWeight;// 空瓶重量
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime weightTime;// 称重时间
    private String projNo;// 项目编号
    private String createdById;// 制单人编码
    private String createdByName;// 制单人名称
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间
    private String createdByOrgId;// 制单人单位编码
    private String createdByOrgName;// 制单人单位名称
    private String orgId;

	@Override
    public Long getId() {
		return id;
	}

	@Override
    public void setId(Long id) {
		this.id = id;
	}

	public String getVocNumber() {
		return vocNumber;
	}

	public void setVocNumber(String vocNumber) {
		this.vocNumber = vocNumber;
	}

	public String getWeighingUserName() {
		return weighingUserName;
	}

	public void setWeighingUserName(String weighingUserName) {
		this.weighingUserName = weighingUserName;
	}

	public String getEmptyBottleWeight() {
		return emptyBottleWeight;
	}

	public void setEmptyBottleWeight(String emptyBottleWeight) {
		this.emptyBottleWeight = emptyBottleWeight;
	}

	public LocalDateTime getWeightTime() {
		return weightTime;
	}

	public void setWeightTime(LocalDateTime weightTime) {
		this.weightTime = weightTime;
	}

	public String getProjNo() {
		return projNo;
	}

	public void setProjNo(String projNo) {
		this.projNo = projNo;
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

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

}
