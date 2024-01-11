package com.sunwayworld.emc.module.bp.reagent.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 一般试剂配制表
 *
 * @author qianc@sunwayworld.com 2019年12月11日
 */
@Table("T_EMC_REAGENT")
public class EmcReagentBean extends AbstractPersistable<Long>implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = 8125865847842454102L;

    @Id
    private Long id;// 主键
    private String reagentName;// 试剂名称
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate prepareDate;// 配制日期
    private String reagentSpecification;// 试剂规格
    private Double dishWeight;// 皿重
    private Double dishReagentWeight;// 皿重+试剂重
    private Double reagentWeight;// 试剂净重
    private String constantVolume;// 定容体积
    private String prepareConcent;// 配制浓度
    private String applicableItem;// 适用项目
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate validDate;// 失效期
    private String remark;// 备注

    private String equiptId; // 仪器ID
    private String equiptName; // 仪器名称

    private String temperature; // 温度
    private String humidity; // 湿度

    private Long methodId; // 方法ID

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

    public String getReagentName() {
        return reagentName;
    }

    public void setReagentName(String reagentName) {
        this.reagentName = reagentName;
    }

    public LocalDate getPrepareDate() {
        return prepareDate;
    }

    public void setPrepareDate(LocalDate prepareDate) {
        this.prepareDate = prepareDate;
    }

    public String getReagentSpecification() {
        return reagentSpecification;
    }

    public void setReagentSpecification(String reagentSpecification) {
        this.reagentSpecification = reagentSpecification;
    }

    public Double getDishWeight() {
        return dishWeight;
    }

    public void setDishWeight(Double dishWeight) {
        this.dishWeight = dishWeight;
    }

    public Double getDishReagentWeight() {
        return dishReagentWeight;
    }

    public void setDishReagentWeight(Double dishReagentWeight) {
        this.dishReagentWeight = dishReagentWeight;
    }

    public Double getReagentWeight() {
        return reagentWeight;
    }

    public void setReagentWeight(Double reagentWeight) {
        this.reagentWeight = reagentWeight;
    }

    public String getConstantVolume() {
        return constantVolume;
    }

    public void setConstantVolume(String constantVolume) {
        this.constantVolume = constantVolume;
    }

    public String getPrepareConcent() {
        return prepareConcent;
    }

    public void setPrepareConcent(String prepareConcent) {
        this.prepareConcent = prepareConcent;
    }

    public String getApplicableItem() {
        return applicableItem;
    }

    public void setApplicableItem(String applicableItem) {
        this.applicableItem = applicableItem;
    }

    public LocalDate getValidDate() {
        return validDate;
    }

    public void setValidDate(LocalDate validDate) {
        this.validDate = validDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getEquiptId() {
        return equiptId;
    }

    public void setEquiptId(String equiptId) {
        this.equiptId = equiptId;
    }

    public String getEquiptName() {
        return equiptName;
    }

    public void setEquiptName(String equiptName) {
        this.equiptName = equiptName;
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

    public Long getMethodId() {
        return methodId;
    }

    public void setMethodId(Long methodId) {
        this.methodId = methodId;
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
