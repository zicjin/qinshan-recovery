/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.samplescheme.resource;

import com.sunwayworld.cloud.module.item.samplescheme.bean.CoreSampleSchemeBean;
import com.sunwayworld.cloud.module.item.samplescheme.bean.CoreSampleSchemeDTO;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/item/sample-schemes"})
public interface CoreSampleSchemeResource
extends GenericCloudResource<CoreSampleSchemeBean, Long> {
    @RequestMapping(value={"/sample-scheme"}, method={RequestMethod.POST})
    public CoreSampleSchemeDTO selectSampleScheme(RestJsonWrapperBean var1);
}
