/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.notification.service;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.item.notification.bean.CoreNotificationInstanceToBean;

public interface CoreNotificationInstanceToService
extends GenericService<CoreNotificationInstanceToBean, Long> {
    public void read(RestJsonWrapperBean var1);

    public void ignore(RestJsonWrapperBean var1);
}
