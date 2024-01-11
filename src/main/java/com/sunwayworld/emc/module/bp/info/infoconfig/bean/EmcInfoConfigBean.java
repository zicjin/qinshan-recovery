package com.sunwayworld.emc.module.bp.info.infoconfig.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 综合信息配置表
 *
 * @author qianc@sunwayworld.com 2019年11月18日
 */
@Table("T_EMC_INFO_CONFIG")
public class EmcInfoConfigBean extends AbstractPersistable<Long>implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = 1072716945444725716L;

    @Id
    private Long id;// 主键

    private String configName;// 名称

    private String configType;//配置类型

    private Long infoTmplId;// 上报模板id

    private Long reportConfigLineId;// 报表ID

    @NotNull(defaultValue = Constant.YES)
    private String display;// 显示

    private String url;// 报表路径

    private String processCategory;

    private Long orderNo;// 排序码

    private Long parentId;// 父ID

    private String createdById;// 制单人编码

    private String createdByName;// 制单人名称

    @JSONField(format = "yyyy-MM-dd")
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

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }


    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    public Long getInfoTmplId() {
        return infoTmplId;
    }

    public void setInfoTmplId(Long infoTmplId) {
        this.infoTmplId = infoTmplId;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
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

    public String getProcessCategory() {
        return processCategory;
    }

    public void setProcessCategory(String processCategory) {
        this.processCategory = processCategory;
    }

    public Long getReportConfigLineId() {
        return reportConfigLineId;
    }

    public void setReportConfigLineId(Long reportConfigLineId) {
        this.reportConfigLineId = reportConfigLineId;
    }



}
