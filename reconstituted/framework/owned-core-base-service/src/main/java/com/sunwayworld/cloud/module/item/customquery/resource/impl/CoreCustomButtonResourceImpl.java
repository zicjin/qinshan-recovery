/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customquery.resource.impl;

import com.sunwayworld.cloud.module.item.customquery.bean.CoreCustomButtonBean;
import com.sunwayworld.cloud.module.item.customquery.resource.CoreCustomButtonResource;
import com.sunwayworld.cloud.module.item.customquery.service.CoreCustomButtonService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.choosable.resource.AbstractGenericChoosableResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u5168\u5c40\u6309\u94ae")
@RestController
@GikamBean
public class CoreCustomButtonResourceImpl
implements CoreCustomButtonResource,
AbstractGenericResource<CoreCustomButtonService, CoreCustomButtonBean, Long>,
AbstractGenericChoosableResource<CoreCustomButtonService, CoreCustomButtonBean, Long> {
    @Autowired
    private CoreCustomButtonService coreCustomButtonService;

    @Override
    public CoreCustomButtonService getService() {
        return this.coreCustomButtonService;
    }

    @Override
    @Log(value="\u65b0\u589e\u81ea\u5b9a\u4e49\u67e5\u8be2", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }
}
