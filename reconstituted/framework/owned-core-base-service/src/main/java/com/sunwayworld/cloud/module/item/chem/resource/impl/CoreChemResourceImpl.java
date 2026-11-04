/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.chem.resource.impl;

import com.sunwayworld.cloud.module.item.chem.bean.CoreChemBean;
import com.sunwayworld.cloud.module.item.chem.resource.CoreChemResource;
import com.sunwayworld.cloud.module.item.chem.service.CoreChemService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@LogModule(value="\u5316\u5b66\u5206\u5b50\u5f0f")
@GikamBean
public class CoreChemResourceImpl
implements CoreChemResource,
AbstractGenericResource<CoreChemService, CoreChemBean, Long> {
    @Autowired
    private CoreChemService chemService;

    @Override
    public CoreChemService getService() {
        return this.chemService;
    }

    @Override
    @Log(value="\u901a\u8fc7TargetId\u83b7\u53d6\u6ee1\u8db3\u6761\u4ef6\u7684\u552f\u4e00\u7684\u9644\u4ef6", type=LogType.SELECT)
    public CoreChemBean selectUniqueByTargetId(@PathVariable String targetId) {
        return this.getService().selectUniqueByTargetId(targetId);
    }
}
