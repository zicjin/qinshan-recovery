/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.rounding.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Clob;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;
import com.sunwayworld.framework.support.domain.Orderable;
import com.sunwayworld.framework.support.domain.Suspendable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_ROUNDING_RULE")
public class CoreRoundingRuleBean
extends AbstractInsertable<String>
implements Insertable<String>,
Orderable<String>,
Suspendable<String> {
    @Transient
    private static final long serialVersionUID = 6766558609247763989L;
    @Id
    private String id;
    private String remark;
    @NotNull(defaultValue="0")
    private String locked;
    private String ruleName;
    @Clob
    private String script;
    private Long orderNo;
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

    public String getRemark() {
        return this.remark;
    }

    public String getLocked() {
        return this.locked;
    }

    public String getRuleName() {
        return this.ruleName;
    }

    public String getScript() {
        return this.script;
    }

    @Override
    public Long getOrderNo() {
        return this.orderNo;
    }

    @Override
    public String getLastSuspendedFlag() {
        return this.lastSuspendedFlag;
    }

    @Override
    public String getLastSuspendedById() {
        return this.lastSuspendedById;
    }

    @Override
    public String getLastSuspendedByName() {
        return this.lastSuspendedByName;
    }

    @Override
    public LocalDateTime getLastSuspendedTime() {
        return this.lastSuspendedTime;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setLocked(String locked) {
        this.locked = locked;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public void setScript(String script) {
        this.script = script;
    }

    @Override
    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    @Override
    public void setLastSuspendedFlag(String lastSuspendedFlag) {
        this.lastSuspendedFlag = lastSuspendedFlag;
    }

    @Override
    public void setLastSuspendedById(String lastSuspendedById) {
        this.lastSuspendedById = lastSuspendedById;
    }

    @Override
    public void setLastSuspendedByName(String lastSuspendedByName) {
        this.lastSuspendedByName = lastSuspendedByName;
    }

    @Override
    public void setLastSuspendedTime(LocalDateTime lastSuspendedTime) {
        this.lastSuspendedTime = lastSuspendedTime;
    }
}
