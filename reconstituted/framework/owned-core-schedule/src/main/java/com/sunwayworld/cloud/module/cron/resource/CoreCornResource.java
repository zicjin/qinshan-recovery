/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.cron.resource;

import com.sunwayworld.cloud.module.cron.bean.CoreCronDTO;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface CoreCornResource {
    @RequestMapping(method={RequestMethod.POST})
    public CoreCronDTO selectCron(RestJsonWrapperBean var1);
}
