package com.sunwayworld.emc.module.mdm.basemdm.testcategory.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 测试类型
 *
 * @author zhangsy@sunwayworld.com 2019/9/6
 *
 */
@Table("T_EMC_TEST_CATEGORY")
public class EmcTestCategoryBean extends AbstractInsertable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = -6058219026762440216L;

    @Id
    private Long id; // 测试类型编码<主键>

    private String testCategoryDesc; // 测试类型描述

    private String departmentOrgId; // 默认科室编码

    private String createdById; // 制单人编码

    private String createdByName; // 制单人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime; // 制单时间

    private String createdByOrgId; // 制单人单位编码

    private String createdByOrgName; // 制单人单位名称

    public String getTestCategoryDesc() {
        return testCategoryDesc;
    }

    public void setTestCategoryDesc(String testCategoryDesc) {
        this.testCategoryDesc = testCategoryDesc;
    }

    public String getDepartmentOrgId() {
        return departmentOrgId;
    }

    public void setDepartmentOrgId(String departmentOrgId) {
        this.departmentOrgId = departmentOrgId;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
