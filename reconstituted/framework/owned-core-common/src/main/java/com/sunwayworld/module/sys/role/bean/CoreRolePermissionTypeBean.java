/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.role.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.support.domain.Suspendable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_ROLE_PERMISSION_TYPE")
public class CoreRolePermissionTypeBean
extends AbstractPersistable<String>
implements Persistable<String>,
Suspendable<String> {
    @Transient
    private static final long serialVersionUID = 929450267646962954L;
    @Id
    private String id;
    private String typeName;
    private String permissionType;
    private String compCategory;
    private String gridPrimaryUrl;
    private String gridColumnSettings;
    private String orderNo;
    @NotNull(defaultValue="0")
    private String lastSuspendedFlag;
    private String lastSuspendedById;
    private String lastSuspendedByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastSuspendedTime;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getCompCategory() {
        return this.compCategory;
    }

    public void setCompCategory(String compCategory) {
        this.compCategory = compCategory;
    }

    public String getPermissionType() {
        return this.permissionType;
    }

    public void setPermissionType(String permissionType) {
        this.permissionType = permissionType;
    }

    public String getGridPrimaryUrl() {
        return this.gridPrimaryUrl;
    }

    public void setGridPrimaryUrl(String gridPrimaryUrl) {
        this.gridPrimaryUrl = gridPrimaryUrl;
    }

    public String getGridColumnSettings() {
        return this.gridColumnSettings;
    }

    public void setGridColumnSettings(String gridColumnSettings) {
        this.gridColumnSettings = gridColumnSettings;
    }

    public String getOrderNo() {
        return this.orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    @Override
    public String getLastSuspendedFlag() {
        return this.lastSuspendedFlag;
    }

    @Override
    public void setLastSuspendedFlag(String lastSuspendedFlag) {
        this.lastSuspendedFlag = lastSuspendedFlag;
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
