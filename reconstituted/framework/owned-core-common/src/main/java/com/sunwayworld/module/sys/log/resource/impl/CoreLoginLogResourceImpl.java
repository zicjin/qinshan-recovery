/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.log.resource.impl;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.log.bean.CoreLoginLogBean;
import com.sunwayworld.module.sys.log.resource.CoreLoginLogResource;
import com.sunwayworld.module.sys.log.service.CoreLoginLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u7cfb\u7edf\u767b\u5f55\u65e5\u5fd7")
@RestController
@GikamBean
public class CoreLoginLogResourceImpl
implements CoreLoginLogResource {
    @Autowired
    private CoreLoginLogService loginLogService;

    @Override
    public CoreLoginLogService getService() {
        return this.loginLogService;
    }

    @Override
    public Page<CoreLoginLogBean> selectOnlinePagination(RestJsonWrapperBean wrapper) {
        return this.getService().selectOnlinePagination(wrapper);
    }

    @Override
    public void offline(RestJsonWrapperBean wrapper) {
        this.getService().offline(wrapper);
    }
}
