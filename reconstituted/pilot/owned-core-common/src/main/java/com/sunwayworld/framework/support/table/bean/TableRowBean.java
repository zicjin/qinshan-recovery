/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.table.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.support.domain.BaseData;
import com.sunwayworld.framework.utils.BeanUtils;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

public class TableRowBean
extends HashMap<String, Object> {
    private static final long serialVersionUID = 7703769909631005974L;
    @JSONField(serialize=false)
    private String pivotText;
    private Map<String, Object> ext$ = new CaseInsensitiveMap<String, Object>();

    public TableRowBean() {
        this.put("ext$", this.getExt$());
    }

    public String getPivotText() {
        return this.pivotText;
    }

    public void setPivotText(String pivotText) {
        this.pivotText = pivotText;
    }

    public Map<String, Object> getExt$() {
        return this.ext$;
    }

    public void setExt$(Map<String, Object> ext$) {
        this.getExt$().putAll(ext$);
    }

    public void setExt$Item(String key, Object value) {
        this.getExt$().put(key, value);
    }

    public Object getExt$Item(String key) {
        return this.getExt$().get(key);
    }

    public String getPivotValue(String pivot) {
        return (String)this.getExt$().get("pivot_" + pivot);
    }

    public String getCellExtValue(String cellField, String ext) {
        return (String)((Map)this.getExt$().get(cellField + "_" + "ext$")).get(ext);
    }

    public String getCellExtIdValue(String cellField) {
        return this.getCellExtValue(cellField, "id");
    }

    public String getCellExtFieldValue(String cellField) {
        return this.getCellExtValue(cellField, "$field");
    }

    public <T extends BaseData> TableRowBean combine(T t) {
        Map<String, Object> tMap = BeanUtils.toMap(t);
        Map ext$Map = (Map)tMap.remove("ext$");
        this.putAll(tMap);
        this.setExt$(ext$Map);
        return this;
    }

    public TableRowBean combine(TableRowBean t) {
        Map ext$Map = (Map)t.remove("ext$");
        this.putAll(t);
        this.setExt$(ext$Map);
        return this;
    }
}
