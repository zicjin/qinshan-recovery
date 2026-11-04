/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.role.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;
import com.sunwayworld.framework.support.domain.Orderable;
import com.sunwayworld.framework.support.domain.Suspendable;
import java.time.LocalDateTime;

@Table(value="T_CORE_ROLE")
public class CoreRoleBean
extends AbstractInsertable<Long>
implements Insertable<Long>,
Suspendable<Long>,
Orderable<Long> {
    @Transient
    private static final long serialVersionUID = 2727145737862448451L;
    @Id
    private Long id;
    private String roleName;
    private String usedByOrgId;
    private String usedByOrgName;
    private String roleDesc;
    private Long passwordPolicyId;
    private Long orderNo;
    @NotNull(defaultValue="0")
    private String lastSuspendedFlag;
    private String lastSuspendedById;
    private String lastSuspendedByName;
    private LocalDateTime lastSuspendedTime;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getRoleName() {
        return this.roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getUsedByOrgId() {
        return this.usedByOrgId;
    }

    public void setUsedByOrgId(String usedByOrgId) {
        this.usedByOrgId = usedByOrgId;
    }

    public String getUsedByOrgName() {
        return this.usedByOrgName;
    }

    public void setUsedByOrgName(String usedByOrgName) {
        this.usedByOrgName = usedByOrgName;
    }

    public String getRoleDesc() {
        return this.roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }

    @Override
    public Long getOrderNo() {
        return this.orderNo;
    }

    @Override
    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public Long getPasswordPolicyId() {
        return this.passwordPolicyId;
    }

    public void setPasswordPolicyId(Long passwordPolicyId) {
        this.passwordPolicyId = passwordPolicyId;
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
