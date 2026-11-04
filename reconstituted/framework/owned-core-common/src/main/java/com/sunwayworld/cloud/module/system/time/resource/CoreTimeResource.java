/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.system.time.resource;

import com.sunwayworld.framework.restful.data.RestHttpTextEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface CoreTimeResource {
    @RequestMapping(method={RequestMethod.GET})
    public RestHttpTextEntity getServerTime();
}
