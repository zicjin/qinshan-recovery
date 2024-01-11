package com.sunwayworld.emc.module.bp.equiptcrves.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 分析仪器曲线
 *
 * @author licm@sunwayworld.com
 * @date 2021-10-21
 */
@Table("T_EMC_ANALYTICAL_EQUIPT_CRVES")
public class PfAnalyticalEquiptCrvesBean extends AbstractInsertable<Long> implements Insertable<Long> {

    @Transient
	private static final long serialVersionUID = -1153212137050065010L;

    @Id
    private Long id;// 主键Id
    private String runId;//批号
    private String sdReserveConcentration;// 标准储备液浓度
    private String solId;//标准溶液批号
    private String sdUseFluidConcentration;//标准使用液浓度
    private String curveFactorFixedTo;//曲线系数定容至
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime addDate;//添加日期
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expirationTime;//过期日期
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime setDate;//配置日期
    private String adder;//添加人
    private String createdById;// 制单人编码
    private String createdByName;// 制单人名称
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间
    private String createdByOrgId;// 制单人单位编码
    private String createdByOrgName;// 制单人单位名称
    private String itemGroup;//分析项组
    private Long itemGroupId;//分析项组ID
    private String stdSubstanceContentUnit;//加标量单位
    private String stdSubstanceContentVolUnit;//加标量体积单位
    private Long testMethodId;
    private String testName;//加标量单位
    private Long methodId;
    private String methodName;
    @NotNull(defaultValue = Constant.NO)
    private String isAbandon;

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

	public String getSdReserveConcentration() {
		return sdReserveConcentration;
	}

	public void setSdReserveConcentration(String sdReserveConcentration) {
		this.sdReserveConcentration = sdReserveConcentration;
	}

	public String getSolId() {
		return solId;
	}

	public void setSolId(String solId) {
		this.solId = solId;
	}

	public String getSdUseFluidConcentration() {
		return sdUseFluidConcentration;
	}

	public void setSdUseFluidConcentration(String sdUseFluidConcentration) {
		this.sdUseFluidConcentration = sdUseFluidConcentration;
	}

	public String getCurveFactorFixedTo() {
		return curveFactorFixedTo;
	}

	public void setCurveFactorFixedTo(String curveFactorFixedTo) {
		this.curveFactorFixedTo = curveFactorFixedTo;
	}

    public LocalDateTime getAddDate() {
        return addDate;
    }

    public void setAddDate(LocalDateTime addDate) {
        this.addDate = addDate;
    }

	public String getAdder() {
		return adder;
	}

	public void setAdder(String adder) {
		this.adder = adder;
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

	public String getItemGroup() {
		return itemGroup;
	}

	public void setItemGroup(String itemGroup) {
		this.itemGroup = itemGroup;
	}

	public Long getItemGroupId() {
		return itemGroupId;
	}

	public void setItemGroupId(Long itemGroupId) {
		this.itemGroupId = itemGroupId;
	}

	public String getStdSubstanceContentUnit() {
		return stdSubstanceContentUnit;
	}

	public void setStdSubstanceContentUnit(String stdSubstanceContentUnit) {
		this.stdSubstanceContentUnit = stdSubstanceContentUnit;
	}

	public String getStdSubstanceContentVolUnit() {
		return stdSubstanceContentVolUnit;
	}

	public void setStdSubstanceContentVolUnit(String stdSubstanceContentVolUnit) {
		this.stdSubstanceContentVolUnit = stdSubstanceContentVolUnit;
	}

	public Long getTestMethodId() {
		return testMethodId;
	}

	public void setTestMethodId(Long testMethodId) {
		this.testMethodId = testMethodId;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public Long getMethodId() {
		return methodId;
	}

	public void setMethodId(Long methodId) {
		this.methodId = methodId;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(LocalDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }

    public LocalDateTime getSetDate() {
        return setDate;
    }

    public void setSetDate(LocalDateTime setDate) {
        this.setDate = setDate;
    }

    public String getIsAbandon() {
        return isAbandon;
    }

    public void setIsAbandon(String isAbandon) {
        this.isAbandon = isAbandon;
    }

}
