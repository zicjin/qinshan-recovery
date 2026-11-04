/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.chem.resource;

import com.sunwayworld.cloud.module.item.chem.bean.CoreChemBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/item/chems"})
public interface CoreChemResource
extends GenericCloudResource<CoreChemBean, Long> {
    @RequestMapping(value={"/filter-target/{targetId}"}, method={RequestMethod.GET})
    public CoreChemBean selectUniqueByTargetId(@PathVariable(value="targetId") String var1);
}
