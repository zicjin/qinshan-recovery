package com.sunwayworld.emc.module.bp.project.projscheme.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractBaseData;
import com.sunwayworld.framework.support.domain.BaseData;

public class EmcProjDTO extends AbstractBaseData implements BaseData {

    @Transient
    private static final long serialVersionUID = 1493332144570718293L;

    private String monitorPointCategoryName;
    private String monitorPointNo;
    private String monitorPointName;
    private Long cycle;
    private Long frequency;
    private String orgName;
    private String itemGroups;

    public String getMonitorPointCategoryName() {
        return monitorPointCategoryName;
    }

    public void setMonitorPointCategoryName(String monitorPointCategoryName) {
        this.monitorPointCategoryName = monitorPointCategoryName;
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

    public Long getCycle() {
        return cycle;
    }

    public void setCycle(Long cycle) {
        this.cycle = cycle;
    }

    public Long getFrequency() {
        return frequency;
    }

    public void setFrequency(Long frequency) {
        this.frequency = frequency;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getItemGroups() {
        return itemGroups;
    }

    public void setItemGroups(String itemGroups) {
        this.itemGroups = itemGroups;
    }

}
