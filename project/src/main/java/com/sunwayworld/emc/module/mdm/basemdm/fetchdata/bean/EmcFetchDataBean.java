package com.sunwayworld.emc.module.mdm.basemdm.fetchdata.bean;

import java.time.LocalDateTime;

import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Activatable;

/**
 * 生物查表
 * @author zhengxy@sunwayworld.com 2020年03月05日
 */
@Table("T_EMC_FETCH_DATA")
public class EmcFetchDataBean extends AbstractInsertable<Long> implements Activatable<Long> {

    @Transient
    private static final long serialVersionUID = -3531090746632416061L;

    @Id
    private Long id;  // 主键
    private String tableName;  // 记录表名称
    private Long fieldOne;  // 名称1数量
    private Long fieldTwo;  // 名称2数量
    private Long fieldThree;  // 名称3数量
    private String fieldOneName;  // 名称1
    private String fieldTwoName;  // 名称2
    private String fieldThreeName;  // 名称3

    @NotNull(defaultValue = Constant.ACTIVATED_STATUS_NO)
    private String activatedFlag;// 启用标志
    private String activatedById;// 启用人编码
    private String activatedByName;// 启用人名称
    private LocalDateTime activatedTime;// 启用时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Long getFieldOne() {
        return fieldOne;
    }

    public void setFieldOne(Long fieldOne) {
        this.fieldOne = fieldOne;
    }

    public Long getFieldTwo() {
        return fieldTwo;
    }

    public void setFieldTwo(Long fieldTwo) {
        this.fieldTwo = fieldTwo;
    }

    public Long getFieldThree() {
        return fieldThree;
    }

    public void setFieldThree(Long fieldThree) {
        this.fieldThree = fieldThree;
    }

    public String getFieldOneName() {
        return fieldOneName;
    }

    public void setFieldOneName(String fieldOneName) {
        this.fieldOneName = fieldOneName;
    }

    public String getFieldTwoName() {
        return fieldTwoName;
    }

    public void setFieldTwoName(String fieldTwoName) {
        this.fieldTwoName = fieldTwoName;
    }

    public String getFieldThreeName() {
        return fieldThreeName;
    }

    public void setFieldThreeName(String fieldThreeName) {
        this.fieldThreeName = fieldThreeName;
    }

    public String getActivatedFlag() {
        return activatedFlag;
    }

    public void setActivatedFlag(String activatedFlag) {
        this.activatedFlag = activatedFlag;
    }

    public String getActivatedById() {
        return activatedById;
    }

    public void setActivatedById(String activatedById) {
        this.activatedById = activatedById;
    }

    public String getActivatedByName() {
        return activatedByName;
    }

    public void setActivatedByName(String activatedByName) {
        this.activatedByName = activatedByName;
    }

    public LocalDateTime getActivatedTime() {
        return activatedTime;
    }

    public void setActivatedTime(LocalDateTime activatedTime) {
        this.activatedTime = activatedTime;
    }
}
