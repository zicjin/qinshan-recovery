/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.sms.service;

import com.sunwayworld.framework.data.ResultEntity;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.sys.sms.bean.CoreSmsBean;
import com.sunwayworld.module.sys.sms.bean.CoreSmsErrorLogBean;

public interface CoreSmsService
extends GenericService<CoreSmsBean, Long> {
    public Page<CoreSmsErrorLogBean> selectErrorLogPagination(Long var1, RestJsonWrapperBean var2);

    public void reset(RestJsonWrapperBean var1);

    public void close(RestJsonWrapperBean var1);

    public void push(String var1, String var2, String var3, String var4);

    public void send(Long var1);

    public ResultEntity send(String var1, String var2, String var3, String var4);
}
