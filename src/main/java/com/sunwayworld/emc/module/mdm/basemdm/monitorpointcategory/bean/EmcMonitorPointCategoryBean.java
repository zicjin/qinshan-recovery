package com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 监测点类型
 *
 * @author magh@sunwayworld.com
 * @date 2019-09-10
 */

@Table("T_EMC_MONITOR_POINT_CATEGORY")
public class EmcMonitorPointCategoryBean extends AbstractPersistable<String> implements Insertable<String> {

    @Transient
    private static final long serialVersionUID = 4093670076581773550L;

    @Id
    private String id;// 监测点类型编码<主键>

    private Long monitorPointType;// 监测点类型

    private Long monitorTmplGroupId;// 监测模板组

    private String orgId; // 单位ID

    private String createdById;// 制单人编码

    private String createdByName;// 制单人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间

    private String createdByOrgId;// 制单人单位编码

    private String createdByOrgName;// 制单人单位名称

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public Long getMonitorPointType() {
        return monitorPointType;
    }

    public void setMonitorPointType(Long monitorPointType) {
        this.monitorPointType = monitorPointType;
    }

    public Long getMonitorTmplGroupId() {
        return monitorTmplGroupId;
    }

    public void setMonitorTmplGroupId(Long monitorTmplGroupId) {
        this.monitorTmplGroupId = monitorTmplGroupId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
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
