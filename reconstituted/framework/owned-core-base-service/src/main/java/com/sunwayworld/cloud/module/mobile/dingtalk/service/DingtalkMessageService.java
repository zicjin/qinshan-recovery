/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.dingtalk.service;

import com.sunwayworld.cloud.module.mobile.dingtalk.bean.DingtalkMessageBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

public interface DingtalkMessageService
extends GenericService<DingtalkMessageBean, Long>,
GenericChoosableService<DingtalkMessageBean, Long> {
    public void send(RestJsonWrapperBean var1);

    public String insertPushMessage(String var1, String var2, String var3, String var4, String var5);

    public String clearPushMessage(String var1, String var2, String var3);

    public String sendNotificationMsg(String var1, String var2, int var3, String var4);
}
