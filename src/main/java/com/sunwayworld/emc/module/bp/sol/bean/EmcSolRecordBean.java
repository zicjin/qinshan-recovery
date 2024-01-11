package com.sunwayworld.emc.module.bp.sol.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 *标定原始记录表
 *
 * @author qianc@sunwayworld.com 2019年12月11日
 */
@Table("T_EMC_SOL_RECORD")
public class EmcSolRecordBean extends AbstractPersistable<Long>implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = 6133439865036575452L;
    @Id
    private Long id;// 主键
    private Long solId;// 标准溶液ID
    private Long titrationNum;// 滴定次数
    private String finalValue;// 终读
    private String startValue;// 始读
    private String consumeValue;// 消耗
    private String unitName;// 单位
    private String orderCategory;// 样品类型
    private String demarcateUserId; // 标定人ID
    private String demarcateUserName; // 标定人
    private String fourRelativeDeviation; // 四次标定结果相对偏差
    private String eightRelativeDeviation; // 八次标定结果相对偏差
    private String samplingVolume; // 取样体积V(ml)
    private String blankStartValue; // 空白用量初读ml
    private String blankFinalValue; // 空白用量终读ml
    private String standardStartValue; // 标液用量初读ml
    private String standardFinalValue; // 标液用量终读ml
    private String calibrationConcentration; // 标定浓度(mol/L)
    private String averageConcentration; // 平均浓度C(mol/L)
    private String remark; // 备注
    private String createdById;// 制单人编码
    private String createdByName;// 制单人名称
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
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

    public String getSamplingVolume() {
        return samplingVolume;
    }

    public void setSamplingVolume(String samplingVolume) {
        this.samplingVolume = samplingVolume;
    }

    public String getBlankStartValue() {
        return blankStartValue;
    }

    public void setBlankStartValue(String blankStartValue) {
        this.blankStartValue = blankStartValue;
    }

    public String getBlankFinalValue() {
        return blankFinalValue;
    }

    public void setBlankFinalValue(String blankFinalValue) {
        this.blankFinalValue = blankFinalValue;
    }

    public String getStandardStartValue() {
        return standardStartValue;
    }

    public void setStandardStartValue(String standardStartValue) {
        this.standardStartValue = standardStartValue;
    }

    public String getStandardFinalValue() {
        return standardFinalValue;
    }

    public void setStandardFinalValue(String standardFinalValue) {
        this.standardFinalValue = standardFinalValue;
    }

    public String getCalibrationConcentration() {
        return calibrationConcentration;
    }

    public void setCalibrationConcentration(String calibrationConcentration) {
        this.calibrationConcentration = calibrationConcentration;
    }

    public String getAverageConcentration() {
        return averageConcentration;
    }

    public void setAverageConcentration(String averageConcentration) {
        this.averageConcentration = averageConcentration;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getFourRelativeDeviation() {
        return fourRelativeDeviation;
    }

    public void setFourRelativeDeviation(String fourRelativeDeviation) {
        this.fourRelativeDeviation = fourRelativeDeviation;
    }

    public String getEightRelativeDeviation() {
        return eightRelativeDeviation;
    }

    public void setEightRelativeDeviation(String eightRelativeDeviation) {
        this.eightRelativeDeviation = eightRelativeDeviation;
    }

    public Long getSolId() {
        return solId;
    }

    public void setSolId(Long solId) {
        this.solId = solId;
    }

    public Long getTitrationNum() {
        return titrationNum;
    }

    public void setTitrationNum(Long titrationNum) {
        this.titrationNum = titrationNum;
    }

    public String getFinalValue() {
        return finalValue;
    }

    public void setFinalValue(String finalValue) {
        this.finalValue = finalValue;
    }

    public String getStartValue() {
        return startValue;
    }

    public void setStartValue(String startValue) {
        this.startValue = startValue;
    }

    public String getConsumeValue() {
        return consumeValue;
    }

    public void setConsumeValue(String consumeValue) {
        this.consumeValue = consumeValue;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getOrderCategory() {
        return orderCategory;
    }

    public void setOrderCategory(String orderCategory) {
        this.orderCategory = orderCategory;
    }

    public String getDemarcateUserId() {
        return demarcateUserId;
    }

    public void setDemarcateUserId(String demarcateUserId) {
        this.demarcateUserId = demarcateUserId;
    }

    public String getDemarcateUserName() {
        return demarcateUserName;
    }

    public void setDemarcateUserName(String demarcateUserName) {
        this.demarcateUserName = demarcateUserName;
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

}
