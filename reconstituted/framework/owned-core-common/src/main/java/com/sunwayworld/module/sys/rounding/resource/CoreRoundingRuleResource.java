/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.rounding.resource;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;
import com.sunwayworld.framework.support.suspendable.resource.GenericSuspendableResource;
import com.sunwayworld.module.sys.rounding.bean.CoreRoundingRuleBean;
import com.sunwayworld.module.sys.rounding.bean.CoreRoundingRuleDTO;
import com.sunwayworld.module.sys.rounding.service.CoreRoundingRuleService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/sys/rounding-rules"})
public interface CoreRoundingRuleResource
extends GenericResource<CoreRoundingRuleService, CoreRoundingRuleBean, String>,
GenericChoosableResource<CoreRoundingRuleService, CoreRoundingRuleBean, String>,
GenericSuspendableResource<CoreRoundingRuleService, CoreRoundingRuleBean, String> {
    @Override
    @RequestMapping(method={RequestMethod.POST})
    public String insert(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/calculate"}, method={RequestMethod.POST})
    public CoreRoundingRuleDTO calculate(RestJsonWrapperBean var1);
}
