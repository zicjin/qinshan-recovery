/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.dingtalk.service;

import com.sunwayworld.cloud.module.mobile.dingtalk.bean.DingtalkUserBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

public interface DingtalkUserService
extends GenericService<DingtalkUserBean, String>,
GenericChoosableService<DingtalkUserBean, String> {
    public void insertPushUser(String var1, String var2, String var3);
}
