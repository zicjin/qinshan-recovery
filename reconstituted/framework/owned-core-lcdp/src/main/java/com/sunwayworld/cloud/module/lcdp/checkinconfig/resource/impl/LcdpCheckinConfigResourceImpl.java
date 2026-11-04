/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.checkinconfig.resource.impl;

import com.sunwayworld.cloud.module.lcdp.checkinconfig.bean.LcdpCheckinConfigBean;
import com.sunwayworld.cloud.module.lcdp.checkinconfig.resource.LcdpCheckinConfigResource;
import com.sunwayworld.cloud.module.lcdp.checkinconfig.service.LcdpCheckinConfigService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.activatable.resource.AbstractGenericActivatableResource;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u8fc1\u5165\u914d\u7f6e\u8868")
@RestController
@GikamBean
public class LcdpCheckinConfigResourceImpl
implements LcdpCheckinConfigResource,
AbstractGenericResource<LcdpCheckinConfigService, LcdpCheckinConfigBean, Long>,
AbstractGenericActivatableResource<LcdpCheckinConfigService, LcdpCheckinConfigBean, Long> {
    @Autowired
    private LcdpCheckinConfigService lcdpCheckinConfigService;

    @Override
    public LcdpCheckinConfigService getService() {
        return this.lcdpCheckinConfigService;
    }

    @Override
    @Log(value="\u65b0\u589e\u8fc1\u5165\u914d\u7f6e\u8868", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }
}
