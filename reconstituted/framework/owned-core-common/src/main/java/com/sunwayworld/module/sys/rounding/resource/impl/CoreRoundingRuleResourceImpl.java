/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.rounding.resource.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.rounding.bean.CoreRoundingRuleDTO;
import com.sunwayworld.module.sys.rounding.resource.CoreRoundingRuleResource;
import com.sunwayworld.module.sys.rounding.service.CoreRoundingRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
@LogModule(value="\u4fee\u7ea6\u89c4\u5219")
@GikamBean
public class CoreRoundingRuleResourceImpl
implements CoreRoundingRuleResource {
    @Autowired
    private CoreRoundingRuleService roundingRuleService;

    @Override
    public CoreRoundingRuleService getService() {
        return this.roundingRuleService;
    }

    @Override
    @Log(value="\u65b0\u589e\u4fee\u7ea6\u89c4\u5219", type=LogType.INSERT)
    public String insert(RestJsonWrapperBean wrapper) {
        return this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u6d4b\u8bd5\u4fee\u7ea6\u89c4\u5219", type=LogType.SELECT)
    public CoreRoundingRuleDTO calculate(RestJsonWrapperBean wrapper) {
        return this.roundingRuleService.calculate(wrapper);
    }
}
