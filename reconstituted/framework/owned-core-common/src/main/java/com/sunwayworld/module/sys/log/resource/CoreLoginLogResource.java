/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.log.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.sys.log.bean.CoreLoginLogBean;
import com.sunwayworld.module.sys.log.service.CoreLoginLogService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/sys/login-logs"})
public interface CoreLoginLogResource
extends GenericResource<CoreLoginLogService, CoreLoginLogBean, Long> {
    @Log(value="\u5728\u7ebf\u7528\u6237\u67e5\u8be2", type=LogType.SELECT)
    @RequestMapping(value={"/queries/online"}, method={RequestMethod.POST})
    public Page<CoreLoginLogBean> selectOnlinePagination(RestJsonWrapperBean var1);

    @Log(value="\u5f3a\u5236\u4e0b\u7ebf", type=LogType.OFFLINE)
    @RequestMapping(value={"/action/offline"}, method={RequestMethod.PUT})
    public void offline(RestJsonWrapperBean var1);
}
