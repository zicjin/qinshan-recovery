package com.sunwayworld.emc.module.mdm.basemdm.mtl.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;
/**
 * @author baogx@sunwayworld.com
 *
 * @date 2019年9月21日 下午3:12:32
 */
@Table("T_EMC_STOCK_CONCENT")
public class EmcStockConcentBean extends AbstractPersistable<Long> implements Insertable<Long> {

    private static final long serialVersionUID = 6252732494247339531L;

    @Id
    private Long id; // 主键

    private Long stockId; // 库存ID

    private String category; // 分类

    private String stdValue; // 标准值

    private String uncertainty; // 不确定度

    private String uncertaintyPercent; // 不确定度%

    private String concentUnitName; // 浓度单位

    private String remark; // 备注

    private String lowLimit; // 低限

    private String highLimit; // 高限

    private Long mtlMaintainId; // 物质维护ID

    private String createdById; // 制单人编码

    private String createdByName; // 制单人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间

    private String createdByOrgId;// 制单人单位编码

    private String createdByOrgName;// 制单人单位名称

    private String revisionRule;// 修约规则

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStockId() {
        return stockId;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStdValue() {
        return stdValue;
    }

    public void setStdValue(String stdValue) {
        this.stdValue = stdValue;
    }

    public String getUncertainty() {
        return uncertainty;
    }

    public void setUncertainty(String uncertainty) {
        this.uncertainty = uncertainty;
    }

    public String getUncertaintyPercent() {
        return uncertaintyPercent;
    }

    public void setUncertaintyPercent(String uncertaintyPercent) {
        this.uncertaintyPercent = uncertaintyPercent;
    }

    public String getConcentUnitName() {
        return concentUnitName;
    }

    public void setConcentUnitName(String concentUnitName) {
        this.concentUnitName = concentUnitName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getLowLimit() {
        return lowLimit;
    }

    public void setLowLimit(String lowLimit) {
        this.lowLimit = lowLimit;
    }

    public String getHighLimit() {
        return highLimit;
    }

    public void setHighLimit(String highLimit) {
        this.highLimit = highLimit;
    }

    public Long getMtlMaintainId() {
        return mtlMaintainId;
    }

    public void setMtlMaintainId(Long mtlMaintainId) {
        this.mtlMaintainId = mtlMaintainId;
    }

    public String getCreatedById() {
        return createdById;
    }

    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getCreatedByOrgId() {
        return createdByOrgId;
    }

    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    public String getCreatedByOrgName() {
        return createdByOrgName;
    }

    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }

    public String getRevisionRule() {
        return revisionRule;
    }

    public void setRevisionRule(String revisionRule) {
        this.revisionRule = revisionRule;
    }
}
