/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.push.service;

import com.sunwayworld.cloud.module.mobile.push.bean.MobilePushMessageBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

public interface MobilePushMessageService
extends GenericService<MobilePushMessageBean, Long>,
GenericChoosableService<MobilePushMessageBean, Long> {
    public void send(RestJsonWrapperBean var1);

    public void insertPushMessage(String var1, String var2, String var3, String var4);
}
