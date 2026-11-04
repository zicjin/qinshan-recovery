/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.configparam.resource.impl;

import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.cloud.module.lcdp.configparam.bean.LcdpConfigParamBean;
import com.sunwayworld.cloud.module.lcdp.configparam.resource.LcdpConfigParamResource;
import com.sunwayworld.cloud.module.lcdp.configparam.service.LcdpConfigParamService;
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

@LogModule(value="\u914d\u7f6e\u53c2\u6570\u8868")
@RestController
@GikamBean
public class LcdpConfigParamResourceImpl
implements LcdpConfigParamResource,
AbstractGenericResource<LcdpConfigParamService, LcdpConfigParamBean, Long> {
    @Autowired
    private LcdpConfigParamService lcdpConfigParamService;

    @Override
    public LcdpConfigParamService getService() {
        return this.lcdpConfigParamService;
    }

    @Override
    @Log(value="\u65b0\u589e\u914d\u7f6e\u53c2\u6570\u8868", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u7528\u6237\u7684\u914d\u7f6e\u53c2\u6570", type=LogType.SELECT)
    public JSONObject selectUserConfigParam(RestJsonWrapperBean wrapper) {
        return this.getService().selectUserConfigParam(wrapper);
    }
}
