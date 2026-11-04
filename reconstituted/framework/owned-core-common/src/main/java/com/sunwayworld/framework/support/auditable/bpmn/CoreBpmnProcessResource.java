/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.auditable.bpmn;

import com.sunwayworld.framework.beans.BeanPropertyDescriptor;
import com.sunwayworld.framework.beans.BeanPropertyHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.support.domain.Auditable;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import java.io.Serializable;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CoreBpmnProcessResource<T extends Auditable<ID>, ID extends Serializable> {
    private final T item;
    private final String tableName;
    private final String statusColumn;
    private String comment;
    private Map<String, Object> vars = new HashMap<String, Object>();

    public CoreBpmnProcessResource(T item, String tableName, String statusColumn) {
        this.item = item;
        this.tableName = tableName;
        this.statusColumn = statusColumn;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public T getItem() {
        return this.item;
    }

    public String getTableName() {
        return this.tableName;
    }

    public String getStatusColumn() {
        return this.statusColumn;
    }

    public Map<String, Object> getVars() {
        return this.vars;
    }

    public void setVars(Map<String, Object> vars) {
        this.vars = Optional.ofNullable(vars).orElseGet(() -> CollectionUtils.emptyMap());
    }

    public void addVars(Map<String, Object> vars) {
        if (vars != null) {
            this.vars.putAll(vars);
        }
    }

    public void addVar(String key, Object value) {
        this.vars.put(key, value);
    }

    public Map<String, Object> getProcessVars() {
        HashMap<String, Object> actVars = new HashMap<String, Object>(this.vars);
        if (this.getItem() != null) {
            List<BeanPropertyDescriptor> propertyDescriptorList = BeanPropertyHelper.getBeanPropertyDescriptorList(this.getItem().getClass());
            propertyDescriptorList.forEach(pd -> {
                if (String.class.equals(pd.getPropertyType()) || Date.class.isAssignableFrom(pd.getPropertyType()) || Temporal.class.isAssignableFrom(pd.getPropertyType()) || Number.class.isAssignableFrom(pd.getPropertyType())) {
                    if (Temporal.class.isAssignableFrom(pd.getPropertyType())) {
                        actVars.put(pd.getName().toLowerCase(), ConvertUtils.convert(pd.getPropertyValue(this.getItem()), Date.class));
                    } else {
                        actVars.put(pd.getName().toLowerCase(), pd.getPropertyValue(this.getItem()));
                    }
                }
            });
            this.getItem().getExt$().forEach((k, v) -> actVars.put(k.toLowerCase(), v));
        }
        if (LocalContextHelper.isUserLogin()) {
            actVars.put("loginUserId", LocalContextHelper.getLoginUserId());
            actVars.put("loginOrgId", LocalContextHelper.getLoginOrgId());
            actVars.put("loginRoleId", LocalContextHelper.getLoginRoleId());
        }
        return actVars;
    }
}
