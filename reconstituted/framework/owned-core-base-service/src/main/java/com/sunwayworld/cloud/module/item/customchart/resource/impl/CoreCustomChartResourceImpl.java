/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customchart.resource.impl;

import com.sunwayworld.cloud.module.item.customchart.bean.CoreCustomChartBean;
import com.sunwayworld.cloud.module.item.customchart.resource.CoreCustomChartResource;
import com.sunwayworld.cloud.module.item.customchart.service.CoreCustomChartService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u81ea\u5b9a\u4e49\u56fe\u8868")
@RestController
@GikamBean
public class CoreCustomChartResourceImpl
implements CoreCustomChartResource,
AbstractGenericResource<CoreCustomChartService, CoreCustomChartBean, Long> {
    @Autowired
    private CoreCustomChartService coreCustomChartService;

    @Override
    public CoreCustomChartService getService() {
        return this.coreCustomChartService;
    }

    @Override
    @Log(value="\u65b0\u589e\u81ea\u5b9a\u4e49\u56fe\u8868", type=LogType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u64a4\u9500\u81ea\u5b9a\u4e49\u56fe\u8868", type=LogType.UPDATE)
    @RequestMapping(value={"/action/revoke"}, method={RequestMethod.PUT})
    public void revoke(RestJsonWrapperBean wrapper) {
        this.getService().revoke(wrapper);
    }

    @Override
    @Log(value="\u53d1\u5e03\u81f3\u83dc\u5355", type=LogType.UPDATE)
    @RequestMapping(value={"/action/publish-menu"}, method={RequestMethod.PUT})
    public void publishToMenu(RestJsonWrapperBean wrapper) {
        this.getService().publishToMenu(wrapper);
    }

    @Override
    @Log(value="\u53d1\u5e03\u81f3\u9996\u9875\u684c\u9762", type=LogType.UPDATE)
    @RequestMapping(value={"/action/publish-wkpanel"}, method={RequestMethod.PUT})
    public void publishToWKPanel(RestJsonWrapperBean wrapper) {
        this.getService().publishToWKPanel(wrapper);
    }

    @Override
    @Log(value="\u590d\u5236", type=LogType.INSERT)
    @RequestMapping(value={"/action/copy"}, method={RequestMethod.PUT})
    public void copy(RestJsonWrapperBean jsonWrapper) {
        this.getService().copy(jsonWrapper);
    }

    @Override
    @Log(value="\u53d8\u66f4", type=LogType.UPDATE)
    @RequestMapping(value={"/action/modify"}, method={RequestMethod.PUT})
    public void modify(RestJsonWrapperBean jsonWrapper) {
        this.getService().modify(jsonWrapper);
    }
}
