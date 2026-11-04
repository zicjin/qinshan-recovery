/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.topology.resource.impl;

import com.sunwayworld.cloud.module.topology.bean.CoreTopologyBean;
import com.sunwayworld.cloud.module.topology.resource.CoreTopologyResource;
import com.sunwayworld.cloud.module.topology.service.CoreTopologyService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@GikamBean
@RestController
@LogModule(value="\u62d3\u6251\u7ed3\u6784\u56fe")
@RequestMapping(value={"/secure/core/module/topologies"})
public class CoreTopologyResourceImpl
implements CoreTopologyResource,
AbstractGenericResource<CoreTopologyService, CoreTopologyBean, Long> {
    @Autowired
    private CoreTopologyService topologyService;

    @Override
    public CoreTopologyService getService() {
        return this.topologyService;
    }

    @Override
    @Log(value="\u65b0\u589e\u62d3\u6251\u7ed3\u6784\u56fe", type=LogType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
        return this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u901a\u8fc7\u7f16\u53f7\u83b7\u53d6\u4fee\u8ba2\u7684\u62d3\u6251\u7ed3\u6784\u56feJSON\u6570\u636e", type=LogType.INSERT)
    public String selectRevisedJsonByCode(@RequestParam String topologyCode) {
        return this.getService().selectRevisedJsonByCode(topologyCode);
    }
}
