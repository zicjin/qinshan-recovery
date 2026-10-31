package com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * 维护事件
 * 
 * @author magh@sunwayworld.com 2019年9月4日
 *
 */
@Table("T_EMC_EQUIPT_MAINT")
public class EmcEquiptMaintBean extends AbstractInsertable<Long> {

    @Transient
    private static final long serialVersionUID = 1908349098986028948L;
    @Id
    private Long id; // 维护事件编码<主键>
    private Long equiptCategoryId; // 仪器类型ID
    private String maintEvent; // 维护事件
    private String eventDesc; // 事件描述
    private Long warningDays; // 提前提醒
    private String sameDay; // 计算当天

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEquiptCategoryId() {
        return equiptCategoryId;
    }

    public void setEquiptCategoryId(Long equiptCategoryId) {
        this.equiptCategoryId = equiptCategoryId;
    }

    public String getMaintEvent() {
        return maintEvent;
    }

    public void setMaintEvent(String maintEvent) {
        this.maintEvent = maintEvent;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    public Long getWarningDays() {
        return warningDays;
    }

    public void setWarningDays(Long warningDays) {
        this.warningDays = warningDays;
    }

    public String getSameDay() {
        return sameDay;
    }

    public void setSameDay(String sameDay) {
        this.sameDay = sameDay;
    }

}
