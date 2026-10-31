package com.sunwayworld.emc.module.bp.equiptcrves.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 分析仪器信息
 *
 * @author licm@sunwayworld.com
 * @date 2021-10-21
 */
@Table("T_EMC_ANALYTICAL_EQUIPT_DETAIL")
public class PfAnalyticalEquiptDetailBean extends AbstractInsertable<Long> implements Insertable<Long> {

	private static final long serialVersionUID = 5417875004947189205L;

    @Id
    private Long id;// 主键Id
    private String runId;//批号
    private String solId;//标准溶液批号
    private String sdSubstances;// 标准物质
    private String analysisNumber;//分析编号
    private String sdSolutionVolume;//标准溶液加入体积ml
    private String sdConcentration;//标准物质浓度ug/L
    private String remark;//备注
    private String qcType ;//qc类型
    private String stdSubstanceContent;//加标量
    private String stdSubstanceContentVol;//加标量体积
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

	public String getSolId() {
		return solId;
	}

	public void setSolId(String solId) {
		this.solId = solId;
	}

	public String getSdSubstances() {
		return sdSubstances;
	}

	public void setSdSubstances(String sdSubstances) {
		this.sdSubstances = sdSubstances;
	}

	public String getAnalysisNumber() {
		return analysisNumber;
	}

	public void setAnalysisNumber(String analysisNumber) {
		this.analysisNumber = analysisNumber;
	}

	public String getSdSolutionVolume() {
		return sdSolutionVolume;
	}

	public void setSdSolutionVolume(String sdSolutionVolume) {
		this.sdSolutionVolume = sdSolutionVolume;
	}

	public String getSdConcentration() {
		return sdConcentration;
	}

	public void setSdConcentration(String sdConcentration) {
		this.sdConcentration = sdConcentration;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	public String getQcType() {
		return qcType;
	}

	public void setQcType(String qcType) {
		this.qcType = qcType;
	}

	public String getStdSubstanceContent() {
		return stdSubstanceContent;
	}

	public void setStdSubstanceContent(String stdSubstanceContent) {
		this.stdSubstanceContent = stdSubstanceContent;
	}

	public String getStdSubstanceContentVol() {
		return stdSubstanceContentVol;
	}

	public void setStdSubstanceContentVol(String stdSubstanceContentVol) {
		this.stdSubstanceContentVol = stdSubstanceContentVol;
	}

}
