// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.bp.bp.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_EMC_EQUIPT_SW_SET")
public class EmcEquiptSwSetBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = -82566196618711043L;
    @Id
    private Long id;
    private Long sampleId;
    private String temperatureOne;
    private String temperatureTwo;
    private String tempDiff;
    private String measureSafe;
    private String checkDiff;
    private String checkSafe;
    private String createdById;
    private String createdByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String createdByOrgId;
    private String createdByOrgName;
    private Long orderId;
    private String orderNo;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getSampleId() {
        return this.sampleId;
    }

    public void setSampleId(Long sampleId) {
        this.sampleId = sampleId;
    }

    public String getTemperatureOne() {
        return this.temperatureOne;
    }

    public void setTemperatureOne(String temperatureOne) {
        this.temperatureOne = temperatureOne;
    }

    public String getTemperatureTwo() {
        return this.temperatureTwo;
    }

    public void setTemperatureTwo(String temperatureTwo) {
        this.temperatureTwo = temperatureTwo;
    }

    public String getTempDiff() {
        return this.tempDiff;
    }

    public void setTempDiff(String tempDiff) {
        this.tempDiff = tempDiff;
    }

    public String getMeasureSafe() {
        return this.measureSafe;
    }

    public void setMeasureSafe(String measureSafe) {
        this.measureSafe = measureSafe;
    }

    public String getCheckDiff() {
        return this.checkDiff;
    }

    public void setCheckDiff(String checkDiff) {
        this.checkDiff = checkDiff;
    }

    public String getCheckSafe() {
        return this.checkSafe;
    }

    public void setCheckSafe(String checkSafe) {
        this.checkSafe = checkSafe;
    }

    @Override
    public String getCreatedById() {
        return this.createdById;
    }

    @Override
    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    @Override
    public String getCreatedByName() {
        return this.createdByName;
    }

    @Override
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    @Override
    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    @Override
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String getCreatedByOrgId() {
        return this.createdByOrgId;
    }

    @Override
    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    @Override
    public String getCreatedByOrgName() {
        return this.createdByOrgName;
    }

    @Override
    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }

    public Long getOrderId() {
        return this.orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return this.orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
