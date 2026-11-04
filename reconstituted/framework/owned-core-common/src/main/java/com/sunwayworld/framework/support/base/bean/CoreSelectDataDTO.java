/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.base.bean;

import com.sunwayworld.framework.support.domain.AbstractBaseData;
import java.util.List;
import java.util.Map;

public class CoreSelectDataDTO
extends AbstractBaseData {
    private static final long serialVersionUID = 7205081622411099371L;
    private String valueField;
    private String textField;
    private List<Map<String, Object>> data;

    public String getValueField() {
        return this.valueField;
    }

    public void setValueField(String valueField) {
        this.valueField = valueField;
    }

    public String getTextField() {
        return this.textField;
    }

    public void setTextField(String textField) {
        this.textField = textField;
    }

    public List<Map<String, Object>> getData() {
        return this.data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }
}
