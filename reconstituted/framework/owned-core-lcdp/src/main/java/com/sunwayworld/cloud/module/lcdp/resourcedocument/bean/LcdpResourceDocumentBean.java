/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resourcedocument.bean;

import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceBean;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceHistoryBean;
import java.io.Serializable;

public class LcdpResourceDocumentBean
implements Serializable {
    private static final long serialVersionUID = -5665008455656802675L;
    private String id;
    private String resourceName;
    private String resourcePath;
    private String resourceCategory;
    private String resourceContent;
    private Long version;
    private String type;
    private String createdById;

    public LcdpResourceDocumentBean(String id, String resourceName, String resourcePath, String resourceCategory, String resourceContent, Long version, String type, String createdById) {
        this.id = id;
        this.resourceName = resourceName;
        this.resourcePath = resourcePath;
        this.resourceCategory = resourceCategory;
        this.resourceContent = resourceContent;
        this.version = version;
        this.type = type;
        this.createdById = createdById;
    }

    public LcdpResourceDocumentBean(LcdpResourceBean resource) {
        this.id = String.valueOf(resource.getId());
        this.resourceName = resource.getResourceName();
        this.resourcePath = resource.getPath();
        this.resourceCategory = resource.getResourceCategory();
        this.resourceContent = resource.getContent();
        this.version = resource.getEffectVersion() == null ? 0L : resource.getEffectVersion();
        this.type = "1";
        this.createdById = resource.getCreatedById();
    }

    public LcdpResourceDocumentBean(LcdpResourceHistoryBean resource) {
        this.id = String.valueOf(resource.getId());
        this.resourceName = resource.getResourceName();
        this.resourcePath = resource.getPath();
        this.resourceCategory = resource.getResourceCategory();
        this.resourceContent = resource.getContent();
        this.version = resource.getVersion();
        this.type = "0";
        this.createdById = resource.getCreatedById();
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResourceName() {
        return this.resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourcePath() {
        return this.resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getResourceCategory() {
        return this.resourceCategory;
    }

    public void setResourceCategory(String resourceCategory) {
        this.resourceCategory = resourceCategory;
    }

    public String getResourceContent() {
        return this.resourceContent;
    }

    public void setResourceContent(String resourceContent) {
        this.resourceContent = resourceContent;
    }

    public Long getVersion() {
        return this.version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreatedById() {
        return this.createdById;
    }

    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }
}
