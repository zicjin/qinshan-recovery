/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resourcelock.resource;

import com.sunwayworld.cloud.module.lcdp.resourcelock.bean.LcdpResourceLockBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/lcdp/resource-locks"})
public interface LcdpResourceLockResource
extends GenericCloudResource<LcdpResourceLockBean, Long> {
    @RequestMapping(value={"/resources/{resourceId}/action/validate-editable"}, method={RequestMethod.POST})
    public RestValidationResultBean validateResourceEditable(@PathVariable String var1);
}
