/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.remotecall.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.suspendable.resource.GenericSuspendableResource;
import com.sunwayworld.module.sys.remotecall.bean.CoreRemoteCallConfigBean;
import com.sunwayworld.module.sys.remotecall.bean.CoreRemoteCallRequestBean;
import com.sunwayworld.module.sys.remotecall.bean.CoreRemoteCallResponseBean;
import com.sunwayworld.module.sys.remotecall.service.CoreRemoteCallConfigService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/sys/remote-calls"})
public interface CoreRemoteCallConfigResource
extends GenericResource<CoreRemoteCallConfigService, CoreRemoteCallConfigBean, String>,
GenericSuspendableResource<CoreRemoteCallConfigService, CoreRemoteCallConfigBean, String> {
    @RequestMapping(value={"/configs/{id}/requests/queries"}, method={RequestMethod.POST})
    public Page<CoreRemoteCallRequestBean> selectRequestPaginationByConfigId(@PathVariable String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/configs/requests/{id}/responses/queries"}, method={RequestMethod.POST})
    public Page<CoreRemoteCallResponseBean> selectResponsePaginationByRequestId(@PathVariable String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/configs/requests/action/reset"}, method={RequestMethod.PUT})
    public void resetRequest(RestJsonWrapperBean var1);

    @RequestMapping(value={"/configs/requests/{id}"}, method={RequestMethod.GET})
    public CoreRemoteCallRequestBean selectRequestById(@PathVariable Long var1, RestJsonWrapperBean var2);
}
