/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customds.bean;

import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomDsJoinFieldDTO;
import java.io.Serializable;
import java.util.List;

public class CoreCustomDsJoinConditionDTO
implements Serializable {
    private static final long serialVersionUID = -957430903459793005L;
    private String joinType;
    private String mainModelId;
    private String subModelId;
    private String mainModelName;
    private String subModelName;
    private List<CoreCustomDsJoinFieldDTO> joinFields;

    public String getJoinType() {
        return this.joinType;
    }

    public void setJoinType(String joinType) {
        this.joinType = joinType;
    }

    public String getMainModelId() {
        return this.mainModelId;
    }

    public void setMainModelId(String mainModelId) {
        this.mainModelId = mainModelId;
    }

    public String getSubModelId() {
        return this.subModelId;
    }

    public void setSubModelId(String subModelId) {
        this.subModelId = subModelId;
    }

    public String getMainModelName() {
        return this.mainModelName;
    }

    public void setMainModelName(String mainModelName) {
        this.mainModelName = mainModelName;
    }

    public String getSubModelName() {
        return this.subModelName;
    }

    public void setSubModelName(String subModelName) {
        this.subModelName = subModelName;
    }

    public List<CoreCustomDsJoinFieldDTO> getJoinFields() {
        return this.joinFields;
    }

    public void setJoinFields(List<CoreCustomDsJoinFieldDTO> joinFields) {
        this.joinFields = joinFields;
    }
}
