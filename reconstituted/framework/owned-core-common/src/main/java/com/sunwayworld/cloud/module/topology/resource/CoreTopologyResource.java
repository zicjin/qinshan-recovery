/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.topology.resource;

import com.sunwayworld.cloud.module.topology.bean.CoreTopologyBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

public interface CoreTopologyResource
extends GenericCloudResource<CoreTopologyBean, Long> {
    @RequestMapping(path={"/revised-json"}, method={RequestMethod.GET})
    public String selectRevisedJsonByCode(@RequestParam String var1);
}
