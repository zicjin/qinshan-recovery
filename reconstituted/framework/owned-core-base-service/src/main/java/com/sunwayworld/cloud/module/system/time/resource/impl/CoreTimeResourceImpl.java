/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.system.time.resource.impl;

import com.sunwayworld.cloud.module.system.time.resource.CoreTimeResource;
import com.sunwayworld.cloud.module.system.time.service.CoreTimeService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestHttpTextEntity;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u7cfb\u7edf\u65f6\u95f4")
@RestController
@GikamBean
@RequestMapping(value={"/core/module/sys/times"})
public class CoreTimeResourceImpl
implements CoreTimeResource {
    @Autowired
    private CoreTimeService timeService;

    @Override
    @Log(value="\u67e5\u8be2\u670d\u52a1\u5668\u65f6\u95f4", type=LogType.SELECT)
    public RestHttpTextEntity getServerTime() {
        return new RestHttpTextEntity(this.timeService.getServerTime());
    }
}
