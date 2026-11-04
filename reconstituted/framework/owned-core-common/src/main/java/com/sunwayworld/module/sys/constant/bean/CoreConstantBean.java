/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.constant.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

@Table(value="T_CORE_CONSTANT")
public class CoreConstantBean
extends AbstractInsertable<String>
implements Insertable<String> {
    @Transient
    private static final long serialVersionUID = 3310773987871973312L;
    @Id
    private String id;
    private String constantValue;
    private String constantDesc;
    private String classfication;
    private String show;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getConstantValue() {
        return this.constantValue;
    }

    public void setConstantValue(String constantValue) {
        this.constantValue = constantValue;
    }

    public String getConstantDesc() {
        return this.constantDesc;
    }

    public void setConstantDesc(String constantDesc) {
        this.constantDesc = constantDesc;
    }

    public String getClassfication() {
        return this.classfication;
    }

    public void setClassfication(String classfication) {
        this.classfication = classfication;
    }

    public String getShow() {
        return this.show;
    }

    public void setShow(String show) {
        this.show = show;
    }
}
