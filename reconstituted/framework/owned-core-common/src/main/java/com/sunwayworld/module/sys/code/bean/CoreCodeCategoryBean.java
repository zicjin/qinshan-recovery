/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.code.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

@Table(value="T_CORE_CODE_CATEGORY")
public class CoreCodeCategoryBean
extends AbstractInsertable<String>
implements Insertable<String> {
    @Transient
    private static final long serialVersionUID = 4344350360225894956L;
    @Id
    private String id;
    private String codeCategoryName;
    private String codeClassId;
    private String remark;
    private String orderNo;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getCodeCategoryName() {
        return this.codeCategoryName;
    }

    public void setCodeCategoryName(String codeCategoryName) {
        this.codeCategoryName = codeCategoryName;
    }

    public String getCodeClassId() {
        return this.codeClassId;
    }

    public void setCodeClassId(String codeClassId) {
        this.codeClassId = codeClassId;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOrderNo() {
        return this.orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
