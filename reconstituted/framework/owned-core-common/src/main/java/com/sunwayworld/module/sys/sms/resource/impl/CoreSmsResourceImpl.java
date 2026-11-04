/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.sms.resource.impl;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.sms.bean.CoreSmsErrorLogBean;
import com.sunwayworld.module.sys.sms.resource.CoreSmsResource;
import com.sunwayworld.module.sys.sms.service.CoreSmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@GikamBean
@RestController
@LogModule(value="\u77ed\u4fe1")
public class CoreSmsResourceImpl
implements CoreSmsResource {
    @Autowired
    private CoreSmsService smsService;

    @Override
    public CoreSmsService getService() {
        return this.smsService;
    }

    @Override
    public Page<CoreSmsErrorLogBean> selectErrorLogPagination(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectErrorLogPagination(id, wrapper);
    }

    @Override
    public void reset(RestJsonWrapperBean wrapper) {
        this.getService().reset(wrapper);
    }

    @Override
    public void close(RestJsonWrapperBean wrapper) {
        this.getService().close(wrapper);
    }
}
