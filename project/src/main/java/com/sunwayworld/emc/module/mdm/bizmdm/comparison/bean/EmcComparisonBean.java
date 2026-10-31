package com.sunwayworld.emc.module.mdm.bizmdm.comparison.bean;

import org.springframework.data.annotation.Id;

import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * 比对信息表
 * @author zhengxy@sunwayworld.com 2020年08月17日
 */
@Table("T_EMC_COMPARISON")
public class EmcComparisonBean extends AbstractInsertable<Long> {

    @Transient
    private static final long serialVersionUID = -7511063087142486800L;

    @Id
    private Long id;  // 主键
    private Long monitorPointId;  // 监测点ID
    private String monitorPointNo;  // 监测点编码
    private String monitorPointName;  // 监测点名称
    private Long testId;  // 测试ID
    private String testName;  // 测试名称
    private String compareEquipmentName;  // 比对仪器名称
    private String compareEquipmentNo;  // 比对仪器编号
    private String compareEquipmentModel;  // 比对仪器型号
    private String monitorMethod; // 监测方法
    private String lowLimit;  // 检出限
    private String measureRange;  // 测量范围
    private String manufacturer;  // 生产商

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getMonitorPointId() {
        return monitorPointId;
    }

    public void setMonitorPointId(Long monitorPointId) {
        this.monitorPointId = monitorPointId;
    }

    public String getMonitorPointNo() {
        return monitorPointNo;
    }

    public void setMonitorPointNo(String monitorPointNo) {
        this.monitorPointNo = monitorPointNo;
    }

    public String getMonitorPointName() {
        return monitorPointName;
    }

    public void setMonitorPointName(String monitorPointName) {
        this.monitorPointName = monitorPointName;
    }

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getCompareEquipmentName() {
        return compareEquipmentName;
    }

    public void setCompareEquipmentName(String compareEquipmentName) {
        this.compareEquipmentName = compareEquipmentName;
    }

    public String getCompareEquipmentNo() {
        return compareEquipmentNo;
    }

    public void setCompareEquipmentNo(String compareEquipmentNo) {
        this.compareEquipmentNo = compareEquipmentNo;
    }

    public String getCompareEquipmentModel() {
        return compareEquipmentModel;
    }

    public void setCompareEquipmentModel(String compareEquipmentModel) {
        this.compareEquipmentModel = compareEquipmentModel;
    }

    public String getLowLimit() {
        return lowLimit;
    }

    public void setLowLimit(String lowLimit) {
        this.lowLimit = lowLimit;
    }

    public String getMonitorMethod() {
        return monitorMethod;
    }

    public void setMonitorMethod(String monitorMethod) {
        this.monitorMethod = monitorMethod;
    }

    public String getMeasureRange() {
        return measureRange;
    }

    public void setMeasureRange(String measureRange) {
        this.measureRange = measureRange;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

}
