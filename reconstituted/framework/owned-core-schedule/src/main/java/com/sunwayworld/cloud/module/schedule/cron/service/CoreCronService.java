/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.schedule.cron.service;

import com.sunwayworld.cloud.module.cron.bean.CoreCronDTO;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

public interface CoreCronService {
    public CoreCronDTO selectCron(RestJsonWrapperBean var1);
}
