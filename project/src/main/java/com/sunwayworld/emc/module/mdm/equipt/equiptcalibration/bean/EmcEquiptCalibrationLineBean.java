package com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * 仪器校准详情
 * @author zhengxy@sunwayworld.com 2020年08月09日
 */
@Table("T_EMC_EQUIPT_CALIBRATION_LINE")
public class EmcEquiptCalibrationLineBean extends AbstractInsertable<Long> {

    @Transient
    private static final long serialVersionUID = -8992957974156925273L;

    @Id
    private Long id;  // 主键
    private Long calibrationId;  // 校准ID
    private String calibrationPeriod;  // 校准周期
    private String batchNo;  // 校准批号
    private String gasName;  // 气体名称
    private String temperature;  // 校准温度
    private String humidity;  // 校准湿度
    private String pressure; // 校准压力
    private String standResult;  // 标准流量值
    private String rn1;  // 仪器校准值1
    private String rn2;  // 仪器校准值2
    private String rn3;  // 仪器校准值3
    private String avgResult;  // 校准平均值
    private String resultError;  // 示值误差
    private String calibrationResult;  // 校准结果
    private String flowUnit; // 流量单位
    private String mobileId; // 移动端ID
    private String passageway; //通道

    private String lowFlow; // 低流量
    private String middleFlow; // 中流量
    private String highFlow; // 高流量
    private String allowError; // 允许误差

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getCalibrationId() {
        return calibrationId;
    }

    public void setCalibrationId(Long calibrationId) {
        this.calibrationId = calibrationId;
    }

    public String getCalibrationPeriod() {
        return calibrationPeriod;
    }

    public void setCalibrationPeriod(String calibrationPeriod) {
        this.calibrationPeriod = calibrationPeriod;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getGasName() {
        return gasName;
    }

    public void setGasName(String gasName) {
        this.gasName = gasName;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getStandResult() {
        return standResult;
    }

    public void setStandResult(String standResult) {
        this.standResult = standResult;
    }

    public String getRn1() {
        return rn1;
    }

    public void setRn1(String rn1) {
        this.rn1 = rn1;
    }

    public String getRn2() {
        return rn2;
    }

    public void setRn2(String rn2) {
        this.rn2 = rn2;
    }

    public String getRn3() {
        return rn3;
    }

    public void setRn3(String rn3) {
        this.rn3 = rn3;
    }

    public String getAvgResult() {
        return avgResult;
    }

    public void setAvgResult(String avgResult) {
        this.avgResult = avgResult;
    }

    public String getResultError() {
        return resultError;
    }

    public void setResultError(String resultError) {
        this.resultError = resultError;
    }

    public String getCalibrationResult() {
        return calibrationResult;
    }

    public void setCalibrationResult(String calibrationResult) {
        this.calibrationResult = calibrationResult;
    }

    public String getFlowUnit() {
        return flowUnit;
    }

    public void setFlowUnit(String flowUnit) {
        this.flowUnit = flowUnit;
    }

    public String getMobileId() {
        return mobileId;
    }

    public void setMobileId(String mobileId) {
        this.mobileId = mobileId;
    }

    public String getPassageway() {
        return passageway;
    }

    public void setPassageway(String passageway) {
        this.passageway = passageway;
    }

    public String getLowFlow() {
        return lowFlow;
    }

    public void setLowFlow(String lowFlow) {
        this.lowFlow = lowFlow;
    }

    public String getMiddleFlow() {
        return middleFlow;
    }

    public void setMiddleFlow(String middleFlow) {
        this.middleFlow = middleFlow;
    }

    public String getHighFlow() {
        return highFlow;
    }

    public void setHighFlow(String highFlow) {
        this.highFlow = highFlow;
    }

    public String getAllowError() {
        return allowError;
    }

    public void setAllowError(String allowError) {
        this.allowError = allowError;
    }
}
