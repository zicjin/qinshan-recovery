/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.dms.resource.impl;

import com.sunwayworld.cloud.module.dms.bean.CoreDmsCategoryBean;
import com.sunwayworld.cloud.module.dms.resource.CoreDmsCategoryResource;
import com.sunwayworld.cloud.module.dms.service.CoreDmsCategoryService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.tree.resource.AbstractGenericTreeResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@GikamBean
@RestController
@LogModule(value="\u5168\u6587\u68c0\u7d22\u7c7b\u578b")
public class CoreDmsCategoryResourceImpl
implements CoreDmsCategoryResource,
AbstractGenericResource<CoreDmsCategoryService, CoreDmsCategoryBean, String>,
AbstractGenericTreeResource<CoreDmsCategoryService, CoreDmsCategoryBean, String> {
    @Autowired
    private CoreDmsCategoryService dmsCategoryService;

    @Override
    public CoreDmsCategoryService getService() {
        return this.dmsCategoryService;
    }
}
