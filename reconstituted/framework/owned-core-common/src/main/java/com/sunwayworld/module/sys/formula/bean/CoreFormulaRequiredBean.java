/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.formula.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Persistable;

@Table(value="T_CORE_FORMULA_RERUIRED")
public class CoreFormulaRequiredBean
extends AbstractPersistable<Long>
implements Persistable<Long> {
    private static final long serialVersionUID = 4182367946507858342L;
    @Id
    private Long id;
    private String itemId;
    private String requiredItemId;
    private String requiredTestId;
    private String requiredOrderId;
    private String requiredRepeatNo;
    private String requiredFolder;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getItemId() {
        return this.itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getRequiredItemId() {
        return this.requiredItemId;
    }

    public void setRequiredItemId(String requiredItemId) {
        this.requiredItemId = requiredItemId;
    }

    public String getRequiredTestId() {
        return this.requiredTestId;
    }

    public void setRequiredTestId(String requiredTestId) {
        this.requiredTestId = requiredTestId;
    }

    public String getRequiredOrderId() {
        return this.requiredOrderId;
    }

    public void setRequiredOrderId(String requiredOrderId) {
        this.requiredOrderId = requiredOrderId;
    }

    public String getRequiredRepeatNo() {
        return this.requiredRepeatNo;
    }

    public void setRequiredRepeatNo(String requiredRepeatNo) {
        this.requiredRepeatNo = requiredRepeatNo;
    }

    public String getRequiredFolder() {
        return this.requiredFolder;
    }

    public void setRequiredFolder(String requiredFolder) {
        this.requiredFolder = requiredFolder;
    }
}
