/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.config.resource;

import com.sunwayworld.cloud.module.lcdp.config.bean.LcdpConfigCompareDTO;
import com.sunwayworld.cloud.module.lcdp.config.bean.LcdpGlobalConfigBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/lcdp/global-configs"})
public interface LcdpGlobalConfigResource
extends GenericCloudResource<LcdpGlobalConfigBean, Long> {
    @RequestMapping(value={"/{configCode}/content"}, method={RequestMethod.GET})
    public LcdpGlobalConfigBean selectConfigContent(@PathVariable String var1);

    @RequestMapping(value={"/action/submit"}, method={RequestMethod.POST})
    public void submit(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/activate"}, method={RequestMethod.POST})
    public void activate(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/compare"}, method={RequestMethod.POST})
    public LcdpConfigCompareDTO compare(RestJsonWrapperBean var1);
}
