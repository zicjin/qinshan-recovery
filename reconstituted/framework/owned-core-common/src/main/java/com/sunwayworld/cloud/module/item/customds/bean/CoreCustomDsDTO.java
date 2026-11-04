/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customds.bean;

import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomDsFieldBean;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractBaseData;
import java.util.List;

public class CoreCustomDsDTO
extends AbstractBaseData {
    @Transient
    private static final long serialVersionUID = -3862782167922567172L;
    private Long id;
    private String dsName;
    private String dsSql;
    private List<CoreCustomDsFieldBean> fieldList;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDsName() {
        return this.dsName;
    }

    public void setDsName(String dsName) {
        this.dsName = dsName;
    }

    public String getDsSql() {
        return this.dsSql;
    }

    public void setDsSql(String dsSql) {
        this.dsSql = dsSql;
    }

    public List<CoreCustomDsFieldBean> getFieldList() {
        return this.fieldList;
    }

    public void setFieldList(List<CoreCustomDsFieldBean> fieldList) {
        this.fieldList = fieldList;
    }
}
