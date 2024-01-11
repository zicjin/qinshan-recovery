package com.sunwayworld.emc.module.mdm.basemdm.monitorability.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;


/**
 * @author magt@sunwayworld.com
 * @date 2019-10-09
 */

@Table("T_EMC_MONITOR_ABILITY_TEST")
public class EmcMonitorAbilityTestBean extends AbstractInsertable<Long> {

    @Transient
    private static final long serialVersionUID = -5702201187473179175L;
    
    @Id
    private Long id;//主键
    
    private Long monitorAbilityId;//监测能力表ID
    
    private Long testMethodId;//测试方法表ID
    
    private Long methodId;//方法ID
    
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
    public Long getTestMethodId() {
        return testMethodId;
    }

    public void setTestMethodId(Long testMethodId) {
        this.testMethodId = testMethodId;
    }
    public Long getMethodId() {
        return methodId;
    }

    public void setMethodId(Long methodId) {
        this.methodId = methodId;
    }
}
