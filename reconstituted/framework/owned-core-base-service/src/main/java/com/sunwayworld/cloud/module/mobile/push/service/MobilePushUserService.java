/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.push.service;

import com.sunwayworld.cloud.module.mobile.push.bean.MobilePushUserBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

public interface MobilePushUserService
extends GenericService<MobilePushUserBean, String>,
GenericChoosableService<MobilePushUserBean, String> {
    public void insertPushUser(String var1, String var2);
}
