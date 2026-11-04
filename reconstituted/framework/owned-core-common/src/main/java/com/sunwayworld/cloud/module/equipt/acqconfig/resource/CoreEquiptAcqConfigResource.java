/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.acqconfig.resource;

import com.sunwayworld.cloud.module.equipt.acqconfig.bean.CoreEquiptAcqConfigBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableCloudResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/equipt/acq-configs"})
public interface CoreEquiptAcqConfigResource
extends GenericCloudResource<CoreEquiptAcqConfigBean, String>,
GenericChoosableCloudResource<CoreEquiptAcqConfigBean, String> {
    @RequestMapping(value={"/{id}/action/connect"}, method={RequestMethod.PUT})
    public String connect(@PathVariable String var1, RestJsonWrapperBean var2);
}
