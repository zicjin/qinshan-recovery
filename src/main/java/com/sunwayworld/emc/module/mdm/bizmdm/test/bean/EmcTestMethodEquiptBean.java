package com.sunwayworld.emc.module.mdm.bizmdm.test.bean;

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
 * 测试方法关联仪器表
 *
 * @author yuanhh@sunwayworld.com
 * @date 2019-09-19
 */

@Table("T_EMC_TEST_METHOD_EQUIPT")
public class EmcTestMethodEquiptBean extends AbstractPersistable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = 4883496152104291354L;

    @Id
    private Long id;// 主键

    private Long testMethodId;// 测试方法ID

    private String analysisCategory;// 分析类型

    private Long equiptId;// 仪器ID

    @NotNull(defaultValue = Constant.NO)
    private String defaultFlag;// 是否默认

    private String createdById;// 制单人编码

    private String createdByName;// 制单人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间

    private String createdByOrgId;// 制单人单位编码

    private String createdByOrgName;// 制单人单位名称

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTestMethodId() {
        return testMethodId;
    }

    public void setTestMethodId(Long testMethodId) {
        this.testMethodId = testMethodId;
    }

    public String getAnalysisCategory() {
        return analysisCategory;
    }

    public void setAnalysisCategory(String analysisCategory) {
        this.analysisCategory = analysisCategory;
    }

    public Long getEquiptId() {
        return equiptId;
    }

    public void setEquiptId(Long equiptId) {
        this.equiptId = equiptId;
    }

    public String getDefaultFlag() {
        return defaultFlag;
    }

    public void setDefaultFlag(String defaultFlag) {
        this.defaultFlag = defaultFlag;
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
}
