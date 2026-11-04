/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.table.resource.impl;

import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableCompareDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpViewBean;
import com.sunwayworld.cloud.module.lcdp.table.resource.LcdpViewResource;
import com.sunwayworld.cloud.module.lcdp.table.service.LcdpViewService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u4f4e\u4ee3\u7801\u5e73\u53f0\u89c6\u56fe\u5386\u53f2")
@RestController
@GikamBean
public class LcdpViewResourceImpl
implements LcdpViewResource,
AbstractGenericResource<LcdpViewService, LcdpViewBean, Long> {
    @Autowired
    private LcdpViewService lcdpViewService;

    @Override
    public LcdpViewService getService() {
        return this.lcdpViewService;
    }

    @Override
    @Log(value="\u8bbe\u8ba1\u89c6\u56fe", type=LogType.INSERT)
    public Long design(@PathVariable String viewName) {
        return this.getService().design(viewName);
    }

    @Override
    @Log(value="\u68c0\u51fa\u89c6\u56fe", type=LogType.INSERT)
    public void checkout(@PathVariable String viewName) {
        this.getService().checkout(viewName);
    }

    @Override
    @Log(value="\u89c6\u56fe\u5bf9\u6bd4", type=LogType.SELECT)
    public LcdpTableCompareDTO<LcdpViewBean> compare(RestJsonWrapperBean wrapper) {
        return this.getService().compare(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u89c6\u56fe\u540d\u67e5\u8be2\u89c6\u56fe\u4fe1\u606f", type=LogType.SELECT)
    public LcdpViewBean selectPhysicalViewInfo(@PathVariable String viewName) {
        return this.getService().selectPhysicalViewInfoList(viewName);
    }
}
