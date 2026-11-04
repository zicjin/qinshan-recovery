/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.office.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.cloud.module.office.bean.CoreOfficeToolbarLineBean;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.support.domain.AbstractBaseData;
import com.sunwayworld.framework.support.domain.Suspendable;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

public class CoreOfficeToolbarDTO
extends AbstractBaseData
implements Suspendable<Long> {
    private static final long serialVersionUID = -3423362805980847693L;
    private Long id;
    private String menuCategoryName;
    private String menuCategoryCode;
    @NotNull(defaultValue="0")
    private String lastSuspendedFlag;
    private String lastSuspendedById;
    private String lastSuspendedByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastSuspendedTime;
    private List<CoreOfficeToolbarLineBean> toolbarLineList;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getMenuCategoryName() {
        return this.menuCategoryName;
    }

    public void setMenuCategoryName(String menuCategoryName) {
        this.menuCategoryName = menuCategoryName;
    }

    public String getMenuCategoryCode() {
        return this.menuCategoryCode;
    }

    public void setMenuCategoryCode(String menuCategoryCode) {
        this.menuCategoryCode = menuCategoryCode;
    }

    @Override
    public String getLastSuspendedFlag() {
        return this.lastSuspendedFlag;
    }

    @Override
    public void setLastSuspendedFlag(String lastSuspendedFlag) {
        this.lastSuspendedFlag = lastSuspendedFlag;
    }

    public List<CoreOfficeToolbarLineBean> getToolbarLineList() {
        return this.toolbarLineList;
    }

    public void setToolbarLineList(List<CoreOfficeToolbarLineBean> toolbarLineList) {
        this.toolbarLineList = toolbarLineList;
    }

    @Override
    public String getLastSuspendedById() {
        return this.lastSuspendedById;
    }

    @Override
    public void setLastSuspendedById(String lastSuspendedById) {
        this.lastSuspendedById = lastSuspendedById;
    }

    @Override
    public String getLastSuspendedByName() {
        return this.lastSuspendedByName;
    }

    @Override
    public void setLastSuspendedByName(String lastSuspendedByName) {
        this.lastSuspendedByName = lastSuspendedByName;
    }

    @Override
    public LocalDateTime getLastSuspendedTime() {
        return this.lastSuspendedTime;
    }

    @Override
    public void setLastSuspendedTime(LocalDateTime lastSuspendedTime) {
        this.lastSuspendedTime = lastSuspendedTime;
    }
}
