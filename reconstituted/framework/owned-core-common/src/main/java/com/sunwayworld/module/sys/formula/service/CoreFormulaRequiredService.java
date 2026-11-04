/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.formula.service;

import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.sys.formula.bean.CoreFormulaRequiredBean;
import java.util.List;

public interface CoreFormulaRequiredService
extends GenericService<CoreFormulaRequiredBean, Long> {
    public void insert(List<CoreFormulaRequiredBean> var1);
}
