/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.remotecall.resource.impl;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.remotecall.bean.CoreRemoteCallRequestBean;
import com.sunwayworld.module.sys.remotecall.bean.CoreRemoteCallResponseBean;
import com.sunwayworld.module.sys.remotecall.resource.CoreRemoteCallConfigResource;
import com.sunwayworld.module.sys.remotecall.service.CoreRemoteCallConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u8fdc\u7a0b\u8c03\u7528")
@RestController
@GikamBean
public class CoreRemoteCallConfigResourceImpl
implements CoreRemoteCallConfigResource {
    @Autowired
    private CoreRemoteCallConfigService coreRemoteCallConfigService;

    @Override
    public CoreRemoteCallConfigService getService() {
        return this.coreRemoteCallConfigService;
    }

    @Override
    @Log(value="\u6839\u636e\u914d\u7f6eID\u67e5\u8be2\u8bf7\u6c42", type=LogType.SELECT)
    public Page<CoreRemoteCallRequestBean> selectRequestPaginationByConfigId(String id, RestJsonWrapperBean wrapper) {
        return this.coreRemoteCallConfigService.selectRequestPaginationByConfigId(id, wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u8bf7\u6c42ID\u67e5\u8be2\u53cd\u9988", type=LogType.SELECT)
    public Page<CoreRemoteCallResponseBean> selectResponsePaginationByRequestId(String id, RestJsonWrapperBean wrapper) {
        return this.coreRemoteCallConfigService.selectResponsePaginationByRequestId(id, wrapper);
    }

    @Override
    @Log(value="\u91cd\u7f6e\u8bf7\u6c42", type=LogType.UPDATE)
    public void resetRequest(RestJsonWrapperBean wrapper) {
        this.coreRemoteCallConfigService.resetRequest(wrapper);
    }

    @Override
    @Log(value="\u6839\u636eID\u67e5\u8be2\u8bf7\u6c42", type=LogType.SELECT)
    public CoreRemoteCallRequestBean selectRequestById(Long id, RestJsonWrapperBean wrapper) {
        return this.coreRemoteCallConfigService.selectRequestById(id, wrapper);
    }
}
