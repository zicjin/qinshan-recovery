/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.log.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_EXPORT_LOG")
public class CoreExportLogBean
extends AbstractPersistable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = -6630823232675986297L;
    @Id
    private Long id;
    private String fileName;
    private String exportMode;
    private String url;
    @NotNull(defaultValue="1")
    private String success;
    private String menuName;
    private String menuId;
    private String createdById;
    private String createdByName;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
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

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getExportMode() {
        return this.exportMode;
    }

    public void setExportMode(String exportMode) {
        this.exportMode = exportMode;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSuccess() {
        return this.success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMenuId() {
        return this.menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getMenuName() {
        return this.menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
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
