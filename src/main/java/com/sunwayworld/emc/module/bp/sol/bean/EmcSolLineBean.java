package com.sunwayworld.emc.module.bp.sol.bean;

import java.time.LocalDate;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * 标准溶液配制明细
 * @author zhengxy@sunwayworld.com 2020年08月30日
 */
@Table("T_EMC_SOL_LINE")
public class EmcSolLineBean extends AbstractInsertable<Long> {

    @Transient
    private static final long serialVersionUID = -7204787004469572821L;

    @Id
    private Long id;  // 主键
    private Long solId;  // 标定溶液ID
    private String solName;  // 标准溶液名称
    private String solSource;  // 标准试剂源名称
    private String reagentManufactor;  // 生产厂家
    private String batchNo;  // 批号
    private String solCategory;  // 溶液类别
    private String takeWeightOrVolume;  // 取用重量/体积(g/mL)
    private String constantVolume;  // 定容体积(mL)
    private String balanceModel;  // 天平型号及编号
    private String prepareConcent;  // 配制浓度
    private String prepareConcentUnit;  // 配制浓度单位
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate prepareDate;  // 配制日期
    private String prepareUserId;  // 配置人ID
    private String prepareUserName;  // 配置人名称
    private String partakeUserId;  // 共同完成人ID
    private String partakeUserName;  // 共同完成人
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate validDate;  // 过期日期
    private String dilution;  // 稀释
    private String remark;  // 备注
    private String calibrateSolName;  // 标定溶液名称
    private String calibrateSolConcent;  // 标定用浓度
    private String calibrateSolVolume;  // 标定用体积
    private String calibrateSolConcentUnit;  // 标定用浓度单位
    private String calibrateSolVolumeUnit;  // 标定用体积单位
    private String originalConcent;  // 标准溶液原浓度
    private String transferVolume;  // 标准溶液移取体积
    private String toCalibrateSolName;// 待标定溶液名称
    private String toCalibrateSolVolume;// 待标定溶液体积
    private String weightUnitName;// 待标定用溶液体积单位
    private String calibratedConcent;// 标定后溶液浓度
    private String dilutionName;  // 稀释介质名称
    private String dilutionVolume;  // 稀释体积
    private String dilutionConcent;  // 稀释后浓度
    private String ambientTemperature;//环境温度(新锐)
    private String storageCondition;//保存条件(新锐)

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getSolId() {
        return solId;
    }

    public void setSolId(Long solId) {
        this.solId = solId;
    }

    public String getSolName() {
        return solName;
    }

    public void setSolName(String solName) {
        this.solName = solName;
    }

    public String getSolSource() {
        return solSource;
    }

    public void setSolSource(String solSource) {
        this.solSource = solSource;
    }

    public String getReagentManufactor() {
        return reagentManufactor;
    }

    public void setReagentManufactor(String reagentManufactor) {
        this.reagentManufactor = reagentManufactor;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getSolCategory() {
        return solCategory;
    }

    public void setSolCategory(String solCategory) {
        this.solCategory = solCategory;
    }

    public String getTakeWeightOrVolume() {
        return takeWeightOrVolume;
    }

    public void setTakeWeightOrVolume(String takeWeightOrVolume) {
        this.takeWeightOrVolume = takeWeightOrVolume;
    }

    public String getConstantVolume() {
        return constantVolume;
    }

    public void setConstantVolume(String constantVolume) {
        this.constantVolume = constantVolume;
    }

    public String getBalanceModel() {
        return balanceModel;
    }

    public void setBalanceModel(String balanceModel) {
        this.balanceModel = balanceModel;
    }

    public String getPrepareConcent() {
        return prepareConcent;
    }

    public void setPrepareConcent(String prepareConcent) {
        this.prepareConcent = prepareConcent;
    }

    public String getPrepareConcentUnit() {
        return prepareConcentUnit;
    }

    public void setPrepareConcentUnit(String prepareConcentUnit) {
        this.prepareConcentUnit = prepareConcentUnit;
    }

    public LocalDate getPrepareDate() {
        return prepareDate;
    }

    public void setPrepareDate(LocalDate prepareDate) {
        this.prepareDate = prepareDate;
    }

    public String getPrepareUserId() {
        return prepareUserId;
    }

    public void setPrepareUserId(String prepareUserId) {
        this.prepareUserId = prepareUserId;
    }

    public String getPrepareUserName() {
        return prepareUserName;
    }

    public void setPrepareUserName(String prepareUserName) {
        this.prepareUserName = prepareUserName;
    }

    public String getPartakeUserId() {
        return partakeUserId;
    }

    public void setPartakeUserId(String partakeUserId) {
        this.partakeUserId = partakeUserId;
    }

    public String getPartakeUserName() {
        return partakeUserName;
    }

    public void setPartakeUserName(String partakeUserName) {
        this.partakeUserName = partakeUserName;
    }

    public LocalDate getValidDate() {
        return validDate;
    }

    public void setValidDate(LocalDate validDate) {
        this.validDate = validDate;
    }

    public String getDilution() {
        return dilution;
    }

    public void setDilution(String dilution) {
        this.dilution = dilution;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCalibrateSolName() {
        return calibrateSolName;
    }

    public void setCalibrateSolName(String calibrateSolName) {
        this.calibrateSolName = calibrateSolName;
    }

    public String getCalibrateSolConcent() {
        return calibrateSolConcent;
    }

    public void setCalibrateSolConcent(String calibrateSolConcent) {
        this.calibrateSolConcent = calibrateSolConcent;
    }

    public String getCalibrateSolVolume() {
        return calibrateSolVolume;
    }

    public void setCalibrateSolVolume(String calibrateSolVolume) {
        this.calibrateSolVolume = calibrateSolVolume;
    }

    public String getCalibrateSolConcentUnit() {
        return calibrateSolConcentUnit;
    }

    public void setCalibrateSolConcentUnit(String calibrateSolConcentUnit) {
        this.calibrateSolConcentUnit = calibrateSolConcentUnit;
    }

    public String getCalibrateSolVolumeUnit() {
        return calibrateSolVolumeUnit;
    }

    public void setCalibrateSolVolumeUnit(String calibrateSolVolumeUnit) {
        this.calibrateSolVolumeUnit = calibrateSolVolumeUnit;
    }

    public String getOriginalConcent() {
        return originalConcent;
    }

    public void setOriginalConcent(String originalConcent) {
        this.originalConcent = originalConcent;
    }

    public String getTransferVolume() {
        return transferVolume;
    }

    public void setTransferVolume(String transferVolume) {
        this.transferVolume = transferVolume;
    }

    public String getToCalibrateSolName() {
        return toCalibrateSolName;
    }

    public void setToCalibrateSolName(String toCalibrateSolName) {
        this.toCalibrateSolName = toCalibrateSolName;
    }

    public String getToCalibrateSolVolume() {
        return toCalibrateSolVolume;
    }

    public void setToCalibrateSolVolume(String toCalibrateSolVolume) {
        this.toCalibrateSolVolume = toCalibrateSolVolume;
    }

    public String getWeightUnitName() {
        return weightUnitName;
    }

    public void setWeightUnitName(String weightUnitName) {
        this.weightUnitName = weightUnitName;
    }

    public String getCalibratedConcent() {
        return calibratedConcent;
    }

    public void setCalibratedConcent(String calibratedConcent) {
        this.calibratedConcent = calibratedConcent;
    }

    public String getDilutionName() {
        return dilutionName;
    }

    public void setDilutionName(String dilutionName) {
        this.dilutionName = dilutionName;
    }

    public String getDilutionVolume() {
        return dilutionVolume;
    }

    public void setDilutionVolume(String dilutionVolume) {
        this.dilutionVolume = dilutionVolume;
    }

    public String getDilutionConcent() {
        return dilutionConcent;
    }

    public void setDilutionConcent(String dilutionConcent) {
        this.dilutionConcent = dilutionConcent;
    }

    public String getAmbientTemperature() {
        return ambientTemperature;
    }

    public void setAmbientTemperature(String ambientTemperature) {
        this.ambientTemperature = ambientTemperature;
    }

    public String getStorageCondition() {
        return storageCondition;
    }

    public void setStorageCondition(String storageCondition) {
        this.storageCondition = storageCondition;
    }
}
