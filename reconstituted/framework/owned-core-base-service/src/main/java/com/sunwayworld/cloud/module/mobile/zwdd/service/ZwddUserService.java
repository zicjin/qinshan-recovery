/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.zwdd.service;

import com.sunwayworld.cloud.module.mobile.zwdd.bean.ZwddUserBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

public interface ZwddUserService
extends GenericService<ZwddUserBean, String>,
GenericChoosableService<ZwddUserBean, String> {
    public void insertPushUser(String var1, String var2, String var3, String var4, String var5);
}
