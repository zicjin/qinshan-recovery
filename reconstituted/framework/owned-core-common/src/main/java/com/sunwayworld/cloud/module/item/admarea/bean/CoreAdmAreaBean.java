/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.admarea.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;
import org.springframework.beans.factory.annotation.Autowired;

@Table(value="T_CORE_ADM_AREA")
public class CoreAdmAreaBean
extends AbstractInsertable<String>
implements Insertable<String> {
    @Autowired
    private static final long serialVersionUID = -7929029874925152241L;
    @Id
    private String id;
    private String admAreaName;
    private String parentId;
    private Long orderNo;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getAdmAreaName() {
        return this.admAreaName;
    }

    public void setAdmAreaName(String admAreaName) {
        this.admAreaName = admAreaName;
    }

    public String getParentId() {
        return this.parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Long getOrderNo() {
        return this.orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }
}
