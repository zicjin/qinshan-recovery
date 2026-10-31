package com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.bean;

import java.time.LocalDateTime;

import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Activatable;

/**
 * 样品性状描述表
 * 
 * @author magh@sunwayworld.com 2019年9月6日
 */

@Table("T_EMC_ORDER_DESC")
public class EmcOrderDescBean extends AbstractInsertable<Long> implements Activatable<Long> {

    @Transient
    private static final long serialVersionUID = -6026267826246849020L;

    @Id
    private Long id;// 主键

    private Long monitorTmplGroupId;// 监测模板组编码

    private String displayTextId;// 显示文本编码

    private String displayText;// 显示文本

    @NotNull(defaultValue = "1")
    private Long orderNo;// 排序码

    @NotNull(defaultValue = Constant.ACTIVATED_STATUS_YES)
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

    public Long getMonitorTmplGroupId() {
        return monitorTmplGroupId;
    }

    public void setMonitorTmplGroupId(Long monitorTmplGroupId) {
        this.monitorTmplGroupId = monitorTmplGroupId;
    }

    public String getDisplayTextId() {
        return displayTextId;
    }

    public void setDisplayTextId(String displayTextId) {
        this.displayTextId = displayTextId;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public Long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
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
