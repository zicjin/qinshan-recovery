/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.remotecall.service;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;
import com.sunwayworld.framework.support.suspendable.service.GenericSuspendableService;
import com.sunwayworld.module.sys.remotecall.bean.CoreRemoteCallConfigBean;
import com.sunwayworld.module.sys.remotecall.bean.CoreRemoteCallRequestBean;
import com.sunwayworld.module.sys.remotecall.bean.CoreRemoteCallResponseBean;

public interface CoreRemoteCallConfigService
extends GenericService<CoreRemoteCallConfigBean, String>,
GenericSelectableService<CoreRemoteCallConfigBean, String>,
GenericSuspendableService<CoreRemoteCallConfigBean, String> {
    public Page<CoreRemoteCallRequestBean> selectRequestPaginationByConfigId(String var1, RestJsonWrapperBean var2);

    public Page<CoreRemoteCallResponseBean> selectResponsePaginationByRequestId(String var1, RestJsonWrapperBean var2);

    public void resetRequest(RestJsonWrapperBean var1);

    public CoreRemoteCallRequestBean selectRequestById(Long var1, RestJsonWrapperBean var2);
}
