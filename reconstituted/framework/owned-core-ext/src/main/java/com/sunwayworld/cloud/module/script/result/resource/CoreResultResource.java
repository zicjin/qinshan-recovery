/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.script.result.resource;

import com.sunwayworld.cloud.module.script.result.bean.CoreResultBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/script/results"})
public interface CoreResultResource
extends GenericCloudResource<CoreResultBean, Long> {
    @RequestMapping(value={"/action/compute"}, method={RequestMethod.PUT})
    public void compute(RestJsonWrapperBean var1);
}
