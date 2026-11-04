/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.page.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Activatable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_ADMIN_PAGE_CONFIG")
public class CoreAdminPageConfigBean
extends AbstractPersistable<Long>
implements Activatable<Long> {
    private static final long serialVersionUID = 5946426661179340889L;
    @Id
    private Long id;
    private String requestUri;
    private String menuId;
    @NotNull(defaultValue="1")
    private Long version;
    @NotNull(defaultValue="0")
    private String activatedFlag;
    private String activatedById;
    private String activatedByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime activatedTime;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getMenuId() {
        return this.menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public Long getVersion() {
        return this.version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getRequestUri() {
        return this.requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    @Override
    public String getActivatedFlag() {
        return this.activatedFlag;
    }

    @Override
    public void setActivatedFlag(String activatedFlag) {
        this.activatedFlag = activatedFlag;
    }

    @Override
    public String getActivatedById() {
        return this.activatedById;
    }

    @Override
    public void setActivatedById(String activatedById) {
        this.activatedById = activatedById;
    }

    @Override
    public String getActivatedByName() {
        return this.activatedByName;
    }

    @Override
    public void setActivatedByName(String activatedByName) {
        this.activatedByName = activatedByName;
    }

    @Override
    public LocalDateTime getActivatedTime() {
        return this.activatedTime;
    }

    @Override
    public void setActivatedTime(LocalDateTime activatedTime) {
        this.activatedTime = activatedTime;
    }
}
