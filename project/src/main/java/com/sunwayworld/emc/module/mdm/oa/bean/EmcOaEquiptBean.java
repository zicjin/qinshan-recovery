// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.mdm.oa.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_EMC_OA_EQUIPT")
public class EmcOaEquiptBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = -82566196618711043L;
    @Id
    private Long id;
    private String fnamark;
    private LocalDateTime selectdate;
    private String sbbh;
    private Long blongdepartment;
    private String departmentname;
    private LocalDateTime createdatetime;
    private LocalDateTime lastmoddatetime;
    private Long equiptid;
    private String mark;
    private String name;
    private String barcode;
    private String capitalspec;
    private String createdById;
    private String createdByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String createdByOrgId;
    private String createdByOrgName;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getFnamark() {
        return this.fnamark;
    }

    public void setFnamark(String fnamark) {
        this.fnamark = fnamark;
    }

    public LocalDateTime getSelectdate() {
        return this.selectdate;
    }

    public void setSelectdate(LocalDateTime selectdate) {
        this.selectdate = selectdate;
    }

    public String getSbbh() {
        return this.sbbh;
    }

    public void setSbbh(String sbbh) {
        this.sbbh = sbbh;
    }

    public Long getBlongdepartment() {
        return this.blongdepartment;
    }

    public void setBlongdepartment(Long blongdepartment) {
        this.blongdepartment = blongdepartment;
    }

    public String getDepartmentname() {
        return this.departmentname;
    }

    public void setDepartmentname(String departmentname) {
        this.departmentname = departmentname;
    }

    public LocalDateTime getCreatedatetime() {
        return this.createdatetime;
    }

    public void setCreatedatetime(LocalDateTime createdatetime) {
        this.createdatetime = createdatetime;
    }

    public LocalDateTime getLastmoddatetime() {
        return this.lastmoddatetime;
    }

    public void setLastmoddatetime(LocalDateTime lastmoddatetime) {
        this.lastmoddatetime = lastmoddatetime;
    }

    public Long getEquiptid() {
        return this.equiptid;
    }

    public void setEquiptid(Long equiptid) {
        this.equiptid = equiptid;
    }

    public String getMark() {
        return this.mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarcode() {
        return this.barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getCapitalspec() {
        return this.capitalspec;
    }

    public void setCapitalspec(String capitalspec) {
        this.capitalspec = capitalspec;
    }

    @Override
    public String getCreatedById() {
        return this.createdById;
    }

    @Override
    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    @Override
    public String getCreatedByName() {
        return this.createdByName;
    }

    @Override
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    @Override
    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    @Override
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String getCreatedByOrgId() {
        return this.createdByOrgId;
    }

    @Override
    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    @Override
    public String getCreatedByOrgName() {
        return this.createdByOrgName;
    }

    @Override
    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }
}
