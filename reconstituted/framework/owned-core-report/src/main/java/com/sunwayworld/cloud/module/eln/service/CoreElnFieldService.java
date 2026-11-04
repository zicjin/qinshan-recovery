/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.eln.service;

import com.sunwayworld.cloud.module.eln.bean.CoreElnFieldBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface CoreElnFieldService
extends GenericService<CoreElnFieldBean, Long> {
    public void writeBack(String var1, String var2);
}
