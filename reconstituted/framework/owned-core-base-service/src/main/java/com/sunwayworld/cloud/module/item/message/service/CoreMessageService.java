/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.message.service;

import com.sunwayworld.cloud.module.item.message.bean.CoreMessageBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface CoreMessageService
extends GenericService<CoreMessageBean, Long>,
GenericActivatableService<CoreMessageBean, Long> {
    @Override
    public Long insert(RestJsonWrapperBean var1);

    public void insertMessageReceipent(Long var1, RestJsonWrapperBean var2);

    public void deleteMessageReceipent(Long var1, RestJsonWrapperBean var2);

    public void receive(RestJsonWrapperBean var1);

    public void send(String var1, String var2, String ... var3);
}
