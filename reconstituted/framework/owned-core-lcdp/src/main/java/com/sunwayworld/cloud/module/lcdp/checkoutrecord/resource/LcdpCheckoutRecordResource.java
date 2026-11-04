/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.checkoutrecord.resource;

import com.sunwayworld.cloud.module.lcdp.checkoutrecord.bean.LcdpCheckOutDTO;
import com.sunwayworld.cloud.module.lcdp.checkoutrecord.bean.LcdpCheckoutRecordBean;
import com.sunwayworld.cloud.module.lcdp.checkoutrecord.bean.LcdpCrdLogBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/lcdp/checkout-records"})
public interface LcdpCheckoutRecordResource
extends GenericCloudResource<LcdpCheckoutRecordBean, Long> {
    public Page<LcdpCrdLogBean> selectLogPaginationByCheckoutRecordId(Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/action/confirm"}, method={RequestMethod.POST})
    public LcdpCheckOutDTO confirm(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/export"}, method={RequestMethod.POST})
    public LcdpCheckOutDTO export(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/checkout"}, method={RequestMethod.POST})
    public LcdpCheckOutDTO checkout(RestJsonWrapperBean var1);
}
