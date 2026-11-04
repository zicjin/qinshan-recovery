/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.checkoutrecord.service;

import com.sunwayworld.cloud.module.lcdp.checkoutrecord.bean.LcdpCheckOutDTO;
import com.sunwayworld.cloud.module.lcdp.checkoutrecord.bean.LcdpCheckoutRecordBean;
import com.sunwayworld.cloud.module.lcdp.checkoutrecord.bean.LcdpCrdLogBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import java.util.List;

public interface LcdpCheckoutRecordService
extends GenericService<LcdpCheckoutRecordBean, Long> {
    public Page<LcdpCrdLogBean> selectLogPaginationByCheckoutRecordId(Long var1, RestJsonWrapperBean var2);

    public LcdpCheckOutDTO confirm(RestJsonWrapperBean var1);

    public LcdpCheckOutDTO export(RestJsonWrapperBean var1);

    public LcdpCheckOutDTO checkout(RestJsonWrapperBean var1);

    public void doCheckout(List<LcdpCrdLogBean> var1, LcdpCheckOutDTO var2, LcdpCheckoutRecordBean var3);
}
