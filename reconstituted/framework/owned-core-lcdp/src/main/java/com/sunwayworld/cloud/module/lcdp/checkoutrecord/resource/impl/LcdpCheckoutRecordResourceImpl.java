/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.checkoutrecord.resource.impl;

import com.sunwayworld.cloud.module.lcdp.checkoutrecord.bean.LcdpCheckOutDTO;
import com.sunwayworld.cloud.module.lcdp.checkoutrecord.bean.LcdpCheckoutRecordBean;
import com.sunwayworld.cloud.module.lcdp.checkoutrecord.bean.LcdpCrdLogBean;
import com.sunwayworld.cloud.module.lcdp.checkoutrecord.resource.LcdpCheckoutRecordResource;
import com.sunwayworld.cloud.module.lcdp.checkoutrecord.service.LcdpCheckoutRecordService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u8fc1\u51fa\u8bb0\u5f55\u8868")
@RestController
@GikamBean
public class LcdpCheckoutRecordResourceImpl
implements LcdpCheckoutRecordResource,
AbstractGenericResource<LcdpCheckoutRecordService, LcdpCheckoutRecordBean, Long> {
    @Autowired
    private LcdpCheckoutRecordService lcdpCheckoutRecordService;

    @Override
    public LcdpCheckoutRecordService getService() {
        return this.lcdpCheckoutRecordService;
    }

    @Override
    @Log(value="\u65b0\u589e\u8fc1\u51fa\u8bb0\u5f55\u8868", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u8fc1\u51fa\u8bb0\u5f55\u8868ID\u67e5\u8be2\u8fc1\u51fa\u8bb0\u5f55\u65e5\u5fd7\u8868", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/logs/queries"}, method={RequestMethod.POST})
    public Page<LcdpCrdLogBean> selectLogPaginationByCheckoutRecordId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectLogPaginationByCheckoutRecordId(id, wrapper);
    }

    @Override
    @Log(value="\u786e\u8ba4(\u786e\u8ba4\u64cd\u4f5c\u5305\u62ec\u8fc1\u51fa\u548c\u5bfc\u51fa)", type=LogType.INSERT)
    public LcdpCheckOutDTO confirm(RestJsonWrapperBean jsonWrapper) {
        return this.getService().confirm(jsonWrapper);
    }

    @Override
    @Log(value="\u8d44\u6e90\u5bfc\u51fa", type=LogType.SELECT)
    public LcdpCheckOutDTO export(RestJsonWrapperBean jsonWrapper) {
        return this.getService().export(jsonWrapper);
    }

    @Override
    @Log(value="\u8d44\u6e90\u8fc1\u51fa", type=LogType.UPDATE)
    public LcdpCheckOutDTO checkout(RestJsonWrapperBean jsonWrapper) {
        return this.getService().checkout(jsonWrapper);
    }
}
