package com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * 仪器校准
 * @author zhengxy@sunwayworld.com 2020年08月09日
 */
@Table("T_EMC_EQUIPT_CALIBRATION")
public class EmcEquiptCalibrationBean extends AbstractInsertable<Long> {

    @Transient
    private static final long serialVersionUID = -5355670078517254902L;

    @Id
    private Long id;  // 主键
    private Long equiptId;  // 检测仪器ID
    private String calibrationNo;  // 校准编号
    private String calibrationType;  // 校准类型
    private Long calibrationEquiptId;  // 校准仪器ID
    private String calibrationBasis;  // 校准依据
    private String errorScope;  // 误差范围
    private String structure;  // 结构是否完整
    private String screen;  // 屏幕是否正常
    private String logo;  // 标识是否清晰
    private String power;  // 通电是否正常
    private String remark;  // 备注
    private String recorderId;  // 记录人ID
    private String recorder;  // 记录人
    private String reviewerId;  // 复核人ID
    private String reviewer;  // 复核人
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime calibrationDate;  // 校准日期
    private String gasFlow;  // 气体流量
    private String envTemperature;  // 环境温度
    private String envPressure;  // 环境压力
    private String envHumidity;  // 环境湿度
    private String standardGasUnit;  // 标准气体生产单位
    private String standardGasExpireDate;  // 标准气体有效期
    private String status;  // 状态
    private String mobileId; // 移动端ID

    private String calibrationCategory; // 校准种类
    private String stdSoundLevel; // 标准声压级
    private String lowLimit; // 检出限

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getEquiptId() {
        return equiptId;
    }

    public void setEquiptId(Long equiptId) {
        this.equiptId = equiptId;
    }

    public String getCalibrationNo() {
        return calibrationNo;
    }

    public void setCalibrationNo(String calibrationNo) {
        this.calibrationNo = calibrationNo;
    }

    public String getCalibrationType() {
        return calibrationType;
    }

    public void setCalibrationType(String calibrationType) {
        this.calibrationType = calibrationType;
    }

    public Long getCalibrationEquiptId() {
        return calibrationEquiptId;
    }

    public void setCalibrationEquiptId(Long calibrationEquiptId) {
        this.calibrationEquiptId = calibrationEquiptId;
    }

    public String getCalibrationBasis() {
        return calibrationBasis;
    }

    public void setCalibrationBasis(String calibrationBasis) {
        this.calibrationBasis = calibrationBasis;
    }

    public String getErrorScope() {
        return errorScope;
    }

    public void setErrorScope(String errorScope) {
        this.errorScope = errorScope;
    }

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public String getScreen() {
        return screen;
    }

    public void setScreen(String screen) {
        this.screen = screen;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRecorderId() {
        return recorderId;
    }

    public void setRecorderId(String recorderId) {
        this.recorderId = recorderId;
    }

    public String getRecorder() {
        return recorder;
    }

    public void setRecorder(String recorder) {
        this.recorder = recorder;
    }

    public String getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(String reviewerId) {
        this.reviewerId = reviewerId;
    }

    public String getReviewer() {
        return reviewer;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }

    public LocalDateTime getCalibrationDate() {
        return calibrationDate;
    }

    public void setCalibrationDate(LocalDateTime calibrationDate) {
        this.calibrationDate = calibrationDate;
    }

    public String getGasFlow() {
        return gasFlow;
    }

    public void setGasFlow(String gasFlow) {
        this.gasFlow = gasFlow;
    }

    public String getEnvTemperature() {
        return envTemperature;
    }

    public void setEnvTemperature(String envTemperature) {
        this.envTemperature = envTemperature;
    }

    public String getEnvPressure() {
        return envPressure;
    }

    public void setEnvPressure(String envPressure) {
        this.envPressure = envPressure;
    }

    public String getEnvHumidity() {
        return envHumidity;
    }

    public void setEnvHumidity(String envHumidity) {
        this.envHumidity = envHumidity;
    }

    public String getStandardGasUnit() {
        return standardGasUnit;
    }

    public void setStandardGasUnit(String standardGasUnit) {
        this.standardGasUnit = standardGasUnit;
    }

    public String getStandardGasExpireDate() {
        return standardGasExpireDate;
    }

    public void setStandardGasExpireDate(String standardGasExpireDate) {
        this.standardGasExpireDate = standardGasExpireDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMobileId() {
        return mobileId;
    }

    public void setMobileId(String mobileId) {
        this.mobileId = mobileId;
    }

    public String getCalibrationCategory() {
        return calibrationCategory;
    }

    public void setCalibrationCategory(String calibrationCategory) {
        this.calibrationCategory = calibrationCategory;
    }

    public String getStdSoundLevel() {
        return stdSoundLevel;
    }

    public void setStdSoundLevel(String stdSoundLevel) {
        this.stdSoundLevel = stdSoundLevel;
    }

    public String getLowLimit() {
        return lowLimit;
    }

    public void setLowLimit(String lowLimit) {
        this.lowLimit = lowLimit;
    }
}
