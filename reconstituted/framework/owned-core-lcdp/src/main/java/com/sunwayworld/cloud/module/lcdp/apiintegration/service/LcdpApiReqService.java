/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.apiintegration.service;

import com.sunwayworld.cloud.module.lcdp.apiintegration.bean.LcdpApiReqBean;
import com.sunwayworld.cloud.module.lcdp.apiintegration.bean.LcdpApiResBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface LcdpApiReqService
extends GenericService<LcdpApiReqBean, Long> {
    public Page<LcdpApiResBean> selectResPaginationByApiReqId(Long var1, RestJsonWrapperBean var2);

    public Object doProcessRequest(LcdpApiReqBean var1, Object ... var2);

    public void reset(Long var1);

    public void record(LcdpApiReqBean var1, LcdpApiResBean var2);
}
