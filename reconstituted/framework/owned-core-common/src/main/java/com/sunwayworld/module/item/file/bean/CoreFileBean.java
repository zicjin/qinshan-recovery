/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.file.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_FILE")
public class CoreFileBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = 1796113880849958293L;
    @Id
    private Long id;
    private String md5Name;
    private String targetId;
    private String name;
    private String fileExt;
    private String scope;
    private LocalDate validTimeFrom;
    private LocalDate validTimeTo;
    @NotNull(defaultValue="1")
    private String permanent;
    private Long size;
    @NotNull(defaultValue="0")
    private Long times;
    private String ext;
    @NotNull(defaultValue="1")
    private String downloadable;
    @Transient
    private String downloadUrl;
    private String bizCategory;
    private String remark;
    @NotNull(defaultValue="1")
    private Long version;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    @Transient
    private String accept;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getMd5Name() {
        return this.md5Name;
    }

    public void setMd5Name(String md5Name) {
        this.md5Name = md5Name;
    }

    public String getTargetId() {
        return this.targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileExt() {
        return this.fileExt;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    public String getScope() {
        return this.scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public LocalDate getValidTimeFrom() {
        return this.validTimeFrom;
    }

    public void setValidTimeFrom(LocalDate validTimeFrom) {
        this.validTimeFrom = validTimeFrom;
    }

    public LocalDate getValidTimeTo() {
        return this.validTimeTo;
    }

    public void setValidTimeTo(LocalDate validTimeTo) {
        this.validTimeTo = validTimeTo;
    }

    public String getPermanent() {
        return this.permanent;
    }

    public void setPermanent(String permanent) {
        this.permanent = permanent;
    }

    public Long getSize() {
        return this.size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getTimes() {
        return this.times;
    }

    public void setTimes(Long times) {
        this.times = times;
    }

    public String getExt() {
        return this.ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getDownloadable() {
        return this.downloadable;
    }

    public void setDownloadable(String downloadable) {
        this.downloadable = downloadable;
    }

    public String getDownloadUrl() {
        return this.downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getBizCategory() {
        return this.bizCategory;
    }

    public void setBizCategory(String bizCategory) {
        this.bizCategory = bizCategory;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getVersion() {
        return this.version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getAccept() {
        return this.accept;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }
}
