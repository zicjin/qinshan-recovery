/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.sms.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.sys.sms.bean.CoreSmsBean;
import com.sunwayworld.module.sys.sms.bean.CoreSmsErrorLogBean;
import com.sunwayworld.module.sys.sms.service.CoreSmsService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/sys/sms"})
public interface CoreSmsResource
extends GenericResource<CoreSmsService, CoreSmsBean, Long> {
    @Log(value="\u8be6\u7ec6\u4fe1\u606f\u67e5\u8be2", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/error-logs/queries"}, method={RequestMethod.POST})
    public Page<CoreSmsErrorLogBean> selectErrorLogPagination(@PathVariable Long var1, RestJsonWrapperBean var2);

    @Log(value="\u91cd\u7f6e", type=LogType.UPDATE)
    @RequestMapping(value={"/action/reset"}, method={RequestMethod.PUT})
    public void reset(RestJsonWrapperBean var1);

    @Log(value="\u5173\u95ed", type=LogType.UPDATE)
    @RequestMapping(value={"/action/close"}, method={RequestMethod.PUT})
    public void close(RestJsonWrapperBean var1);
}
