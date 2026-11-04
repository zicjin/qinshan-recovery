/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resourceversion.resource;

import com.sunwayworld.cloud.module.lcdp.resourceversion.bean.LcdpResourceVersionBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/lcdp/resource-versions"})
public interface LcdpResourceVersionResource
extends GenericCloudResource<LcdpResourceVersionBean, Long> {
    @RequestMapping(value={"/resources/action/activate"}, method={RequestMethod.PUT})
    public void activateResource(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/deal-history-data"}, method={RequestMethod.PUT})
    public void dealHistoryData(RestJsonWrapperBean var1);
}
