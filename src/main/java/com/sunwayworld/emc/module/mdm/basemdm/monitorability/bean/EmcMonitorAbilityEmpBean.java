package com.sunwayworld.emc.module.mdm.basemdm.monitorability.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;


/**
 * @author magt@sunwayworld.com
 * @date 2019-10-09
 */

@Table("T_EMC_MONITOR_ABILITY_EMP")
public class EmcMonitorAbilityEmpBean extends AbstractInsertable<Long> {

    @Transient
    private static final long serialVersionUID = -7442740295733673362L;
    
    @Id
    private Long id;//主键
    
    private Long monitorAbilityId;//检测能力表ID
    
    private String empId;//人员表ID
    
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
    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }
}
