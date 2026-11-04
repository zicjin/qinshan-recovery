/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.resource.impl;

import com.sunwayworld.cloud.module.les.bean.CoreLesUnitBean;
import com.sunwayworld.cloud.module.les.resource.CoreLesUnitResource;
import com.sunwayworld.cloud.module.les.service.CoreLesUnitService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.choosable.resource.AbstractGenericChoosableResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@LogModule(value="LES\u7ec4\u4ef6")
@GikamBean
@RequestMapping(value={"/secure/core/les/les-units"})
public class CoreLesUnitResourceImpl
implements CoreLesUnitResource,
AbstractGenericResource<CoreLesUnitService, CoreLesUnitBean, String>,
AbstractGenericChoosableResource<CoreLesUnitService, CoreLesUnitBean, String> {
    @Autowired
    private CoreLesUnitService coreLesUnitService;

    @Override
    public CoreLesUnitService getService() {
        return this.coreLesUnitService;
    }
}
