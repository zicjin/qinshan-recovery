package com.sunwayworld.emc.module.bp.sol.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 标准溶液配制表
 *
 * @author qianc@sunwayworld.com 2019年12月11日
 */
@Table("T_EMC_SOL")
public class EmcSolBean extends AbstractPersistable<Long>implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = 4536910647981093253L;

    @Id
    private Long id;// 主键
    private String solName;// 标准溶液名称
    private String solSource;// 标准溶液来源
    private String solNo;// 标准溶液编号
    private String solCategory;// 溶液类别
    private String calucate;// 是否参与计算
    private String parentSolName;// 上级标准物质/溶液
    private String takeWeightOrVolume;// 取用重量/体积
    private String calibSolConstantVolume;// 待标液定容至
    private String partakeUserId;// 共同完成人
    private String partakeUserName;// 共同完成人
    private String prepareUserId;// 配制人
    private String tabCategory;// 页签类型
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate calibrateDate;// 标定日期
    private String correctFactor;// 校正系数K
    private String reagentName;// 试剂名称
    private String reagentLevel;// 试剂等级
    private String reagentManufactor;// 试剂生产厂家及编号
    private String balanceModel;// 称量天平型号及编号
    private String dryTemperature;// 干燥温度
    private String reagentWeight;// 试剂重量
    private String solventName;// 溶剂名称
    private String constantVolume;// 定容体积
    private String prepareTemperature;// 配制温度
    private String prepareConcent;// 配制浓度
    private String calibrateSolName;// 标定用标液名称
    private String calibrateSolConcent;// 标定用标液浓度
    private String toCalibrateSolName;// 待标定溶液名称
    private String toCalibrateSolVolume;// 待标定溶液体积
    private String buretteVolume;// 滴定管体积
    private String calibratedConcent;// 标定后溶液浓度
    private String originalConcent;// 标准溶液原浓度
    private String transferVolume;// 标准溶液移取体积
    private String dilutionName;// 稀释介质名称
    private String prepareConstantVolume;// 标准溶液配制定容体积
    private String preparedConcent;// 标准溶液配制后浓度
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate prepareDate;// 配制日期
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate solPrepareDate;// 标准溶液配制日期
    private String volumeUnitName;// 体积单位
    private String concentUnitName;// 浓度单位
    private String weightUnitName;// 重量单位
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate validDate;// 失效期
    private String script;// 计算公式
    private String createdById;// 制单人编码
    private String createdByName;// 制单人名称
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间
    private String createdByOrgId;// 制单人单位编码
    private String createdByOrgName;// 制单人单位名称

    @NotNull(defaultValue = "0")
    private String status; // 是否发布（0否 1是）

    private String batchNo; // 批号

    private String prepareConcentUnit; // 配制浓度单位

    private String prepareUserName; // 配置人名称

    private String dilution; // 稀释

    private String remark; // 备注

    private String calibrateSolVolume; // 标定用体积

    private String calibrateSolConcentUnit; // 标定用浓度单位

    private String calibrateSolVolumeUnit; // 标定用体积单位

    private String dilutionVolume; // 稀释体积

    private String dilutionConcent; // 稀释后浓度

    @NotNull(defaultValue = "EMC,5")
    private String revisionRule; // 标定后修约规则

    private Long methodId; // 方法

    private String methodName; // 方法名称

    private String surplus;//剩余量(新锐项目)

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getCalibSolConstantVolume() {
        return calibSolConstantVolume;
    }

    public void setCalibSolConstantVolume(String calibSolConstantVolume) {
        this.calibSolConstantVolume = calibSolConstantVolume;
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

    public String getSolNo() {
        return solNo;
    }

    public void setSolNo(String solNo) {
        this.solNo = solNo;
    }

    public String getSolCategory() {
        return solCategory;
    }

    public void setSolCategory(String solCategory) {
        this.solCategory = solCategory;
    }

    public String getCalucate() {
        return calucate;
    }

    public void setCalucate(String calucate) {
        this.calucate = calucate;
    }

    public String getParentSolName() {
        return parentSolName;
    }

    public void setParentSolName(String parentSolName) {
        this.parentSolName = parentSolName;
    }

    public String getTakeWeightOrVolume() {
        return takeWeightOrVolume;
    }

    public void setTakeWeightOrVolume(String takeWeightOrVolume) {
        this.takeWeightOrVolume = takeWeightOrVolume;
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

    public String getPrepareUserId() {
        return prepareUserId;
    }

    public void setPrepareUserId(String prepareUserId) {
        this.prepareUserId = prepareUserId;
    }

    public String getTabCategory() {
        return tabCategory;
    }

    public void setTabCategory(String tabCategory) {
        this.tabCategory = tabCategory;
    }

    public LocalDate getCalibrateDate() {
        return calibrateDate;
    }

    public void setCalibrateDate(LocalDate calibrateDate) {
        this.calibrateDate = calibrateDate;
    }

    public String getCorrectFactor() {
        return correctFactor;
    }

    public void setCorrectFactor(String correctFactor) {
        this.correctFactor = correctFactor;
    }

    public String getReagentName() {
        return reagentName;
    }

    public void setReagentName(String reagentName) {
        this.reagentName = reagentName;
    }

    public String getReagentLevel() {
        return reagentLevel;
    }

    public void setReagentLevel(String reagentLevel) {
        this.reagentLevel = reagentLevel;
    }

    public String getReagentManufactor() {
        return reagentManufactor;
    }

    public void setReagentManufactor(String reagentManufactor) {
        this.reagentManufactor = reagentManufactor;
    }

    public String getBalanceModel() {
        return balanceModel;
    }

    public void setBalanceModel(String balanceModel) {
        this.balanceModel = balanceModel;
    }

    public String getDryTemperature() {
        return dryTemperature;
    }

    public void setDryTemperature(String dryTemperature) {
        this.dryTemperature = dryTemperature;
    }

    public String getReagentWeight() {
        return reagentWeight;
    }

    public void setReagentWeight(String reagentWeight) {
        this.reagentWeight = reagentWeight;
    }

    public String getSolventName() {
        return solventName;
    }

    public void setSolventName(String solventName) {
        this.solventName = solventName;
    }

    public String getConstantVolume() {
        return constantVolume;
    }

    public void setConstantVolume(String constantVolume) {
        this.constantVolume = constantVolume;
    }

    public String getPrepareTemperature() {
        return prepareTemperature;
    }

    public void setPrepareTemperature(String prepareTemperature) {
        this.prepareTemperature = prepareTemperature;
    }

    public String getPrepareConcent() {
        return prepareConcent;
    }

    public void setPrepareConcent(String prepareConcent) {
        this.prepareConcent = prepareConcent;
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

    public String getBuretteVolume() {
        return buretteVolume;
    }

    public void setBuretteVolume(String buretteVolume) {
        this.buretteVolume = buretteVolume;
    }

    public String getCalibratedConcent() {
        return calibratedConcent;
    }

    public void setCalibratedConcent(String calibratedConcent) {
        this.calibratedConcent = calibratedConcent;
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

    public String getDilutionName() {
        return dilutionName;
    }

    public void setDilutionName(String dilutionName) {
        this.dilutionName = dilutionName;
    }

    public String getPrepareConstantVolume() {
        return prepareConstantVolume;
    }

    public void setPrepareConstantVolume(String prepareConstantVolume) {
        this.prepareConstantVolume = prepareConstantVolume;
    }

    public String getPreparedConcent() {
        return preparedConcent;
    }

    public void setPreparedConcent(String preparedConcent) {
        this.preparedConcent = preparedConcent;
    }

    public LocalDate getPrepareDate() {
        return prepareDate;
    }

    public void setPrepareDate(LocalDate prepareDate) {
        this.prepareDate = prepareDate;
    }

    public LocalDate getSolPrepareDate() {
        return solPrepareDate;
    }

    public void setSolPrepareDate(LocalDate solPrepareDate) {
        this.solPrepareDate = solPrepareDate;
    }

    public String getVolumeUnitName() {
        return volumeUnitName;
    }

    public void setVolumeUnitName(String volumeUnitName) {
        this.volumeUnitName = volumeUnitName;
    }

    public String getConcentUnitName() {
        return concentUnitName;
    }

    public void setConcentUnitName(String concentUnitName) {
        this.concentUnitName = concentUnitName;
    }

    public String getWeightUnitName() {
        return weightUnitName;
    }

    public void setWeightUnitName(String weightUnitName) {
        this.weightUnitName = weightUnitName;
    }

    public LocalDate getValidDate() {
        return validDate;
    }

    public void setValidDate(LocalDate validDate) {
        this.validDate = validDate;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getPrepareConcentUnit() {
        return prepareConcentUnit;
    }

    public void setPrepareConcentUnit(String prepareConcentUnit) {
        this.prepareConcentUnit = prepareConcentUnit;
    }

    public String getPrepareUserName() {
        return prepareUserName;
    }

    public void setPrepareUserName(String prepareUserName) {
        this.prepareUserName = prepareUserName;
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

    public String getRevisionRule() {
        return revisionRule;
    }

    public void setRevisionRule(String revisionRule) {
        this.revisionRule = revisionRule;
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

    public String getSurplus() {
        return surplus;
    }

    public void setSurplus(String surplus) {
        this.surplus = surplus;
    }
}
