/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.bean;

import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpModulePageCompBean;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceHistoryBean;
import com.sunwayworld.framework.support.domain.AbstractBaseData;
import java.util.List;
import java.util.Map;

public class LcdpResourceDTO
extends AbstractBaseData {
    private static final long serialVersionUID = -6472221143398378503L;
    private Long id;
    private Long resourceId;
    private String name;
    private String type;
    private String content;
    private Boolean editable;
    private List<LcdpModulePageCompBean> components;
    private String path;
    private String clientScript;
    private String resourceStatus;
    private Map<String, Map<String, String>> i18n;
    private List<String> dependentI18n;
    private String pageConfig;

    public static LcdpResourceDTO of(LcdpResourceHistoryBean resourceHistory) {
        LcdpResourceDTO resourceDTO = new LcdpResourceDTO();
        resourceDTO.setId(resourceHistory.getId());
        resourceDTO.setResourceId(resourceHistory.getResourceId());
        resourceDTO.setName(resourceHistory.getResourceName());
        resourceDTO.setType(resourceHistory.getResourceCategory());
        resourceDTO.setContent(resourceHistory.getContent());
        resourceDTO.setPath(resourceHistory.getPath());
        return resourceDTO;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getResourceId() {
        return this.resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getEditable() {
        return this.editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public List<LcdpModulePageCompBean> getComponents() {
        return this.components;
    }

    public void setComponents(List<LcdpModulePageCompBean> components) {
        this.components = components;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getClientScript() {
        return this.clientScript;
    }

    public void setClientScript(String clientScript) {
        this.clientScript = clientScript;
    }

    public String getResourceStatus() {
        return this.resourceStatus;
    }

    public void setResourceStatus(String resourceStatus) {
        this.resourceStatus = resourceStatus;
    }

    public List<String> getDependentI18n() {
        return this.dependentI18n;
    }

    public void setDependentI18n(List<String> dependentI18n) {
        this.dependentI18n = dependentI18n;
    }

    public Map<String, Map<String, String>> getI18n() {
        return this.i18n;
    }

    public void setI18n(Map<String, Map<String, String>> i18n) {
        this.i18n = i18n;
    }

    public String getPageConfig() {
        return this.pageConfig;
    }

    public void setPageConfig(String pageConfig) {
        this.pageConfig = pageConfig;
    }
}
