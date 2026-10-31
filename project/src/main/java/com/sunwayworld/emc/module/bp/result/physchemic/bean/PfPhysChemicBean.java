package com.sunwayworld.emc.module.bp.result.physchemic.bean;



import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 分析录入理化记录表
 *
 * @author licm@sunwayworld.com
 * @date 2022-07-12
 */
@Table("T_EMC_PHYS_CHEMIC")
public class PfPhysChemicBean extends AbstractInsertable<Long> implements Insertable<Long> {

    /**
	 * 
	 */
	private static final long serialVersionUID = -214074103931352547L;
	@Transient


	@Id
    private Long id;// 主键Id
    private String runId;//批号
    private String waterTemp;//水温
    private String stdSol;//标准溶液
    private String measuredValue;//测定值
    private String calibrationValues;//校准值
    private String zeroOxygenCalibration;//零氧标定
    private String fullnessCalibration;//满度标定
    private String remark; 
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

	public String getRunId() {
		return runId;
	}

	public void setRunId(String runId) {
		this.runId = runId;
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

	public String getWaterTemp() {
		return waterTemp;
	}

	public void setWaterTemp(String waterTemp) {
		this.waterTemp = waterTemp;
	}

	public String getStdSol() {
		return stdSol;
	}

	public void setStdSol(String stdSol) {
		this.stdSol = stdSol;
	}

	public String getMeasuredValue() {
		return measuredValue;
	}

	public void setMeasuredValue(String measuredValue) {
		this.measuredValue = measuredValue;
	}

	public String getCalibrationValues() {
		return calibrationValues;
	}

	public void setCalibrationValues(String calibrationValues) {
		this.calibrationValues = calibrationValues;
	}

	public String getZeroOxygenCalibration() {
		return zeroOxygenCalibration;
	}

	public void setZeroOxygenCalibration(String zeroOxygenCalibration) {
		this.zeroOxygenCalibration = zeroOxygenCalibration;
	}

	public String getFullnessCalibration() {
		return fullnessCalibration;
	}

	public void setFullnessCalibration(String fullnessCalibration) {
		this.fullnessCalibration = fullnessCalibration;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}


}
