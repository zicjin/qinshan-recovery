package com.sunwayworld.emc.module.mdm.basemdm.monitorability.bean;

import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * @author magt@sunwayworld.com
 * @date 2019-10-09
 */

@Table("T_EMC_MONITOR_ABILITY_EQUIPT")
public class EmcMonitorAbilityEquiptBean extends AbstractInsertable<Long> {

    @Transient
    private static final long serialVersionUID = -3722526599740122570L;

    @Id
    private Long id;// 主键

    private Long monitorAbilityId;// 检测能力表ID

    private Long equiptId;// 仪器表ID

    private String departmentId;// 科室ID

    @NotNull(defaultValue = Constant.NO)
    private String defaultFlag;// 是否默认

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMonitorAbilityId() {
        return monitorAbilityId;
    }

    public void setMonitorAbilityId(Long monitorAbilityId) {
        this.monitorAbilityId = monitorAbilityId;
    }

    public Long getEquiptId() {
        return equiptId;
    }

    public void setEquiptId(Long equiptId) {
        this.equiptId = equiptId;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getDefaultFlag() {
        return defaultFlag;
    }

    public void setDefaultFlag(String defaultFlag) {
        this.defaultFlag = defaultFlag;
    }
}
