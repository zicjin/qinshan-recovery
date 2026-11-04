/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customds.bean;

import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomColumnDTO;
import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomDsJoinConditionDTO;
import java.io.Serializable;
import java.util.List;

public class CoreDsModelDTO
implements Serializable {
    private static final long serialVersionUID = 8145255636062046364L;
    private String id;
    private List<CoreDsModelDTO> children;
    private String parentId;
    private List<CoreCustomColumnDTO> columns;
    private String modelId;
    private String modelName;
    private String modelType;
    private CoreCustomDsJoinConditionDTO joinConditions;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<CoreDsModelDTO> getChildren() {
        return this.children;
    }

    public void setChildren(List<CoreDsModelDTO> children) {
        this.children = children;
    }

    public String getParentId() {
        return this.parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public List<CoreCustomColumnDTO> getColumns() {
        return this.columns;
    }

    public void setColumns(List<CoreCustomColumnDTO> columns) {
        this.columns = columns;
    }

    public String getModelId() {
        return this.modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getModelName() {
        return this.modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelType() {
        return this.modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public CoreCustomDsJoinConditionDTO getJoinConditions() {
        return this.joinConditions;
    }

    public void setJoinConditions(CoreCustomDsJoinConditionDTO joinConditions) {
        this.joinConditions = joinConditions;
    }
}
