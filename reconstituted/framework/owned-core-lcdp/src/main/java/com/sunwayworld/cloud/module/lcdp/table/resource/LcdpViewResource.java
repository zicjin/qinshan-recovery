/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.table.resource;

import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableCompareDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpViewBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/lcdp/views"})
public interface LcdpViewResource
extends GenericCloudResource<LcdpViewBean, Long> {
    @RequestMapping(value={"/{viewName}/action/design"}, method={RequestMethod.POST})
    public Long design(String var1);

    @RequestMapping(value={"/{viewName}/action/checkout"}, method={RequestMethod.POST})
    public void checkout(String var1);

    @RequestMapping(value={"/action/compare"}, method={RequestMethod.POST})
    public LcdpTableCompareDTO<LcdpViewBean> compare(RestJsonWrapperBean var1);

    @RequestMapping(value={"/{viewName}/info/physical"}, method={RequestMethod.GET})
    public LcdpViewBean selectPhysicalViewInfo(String var1);
}
