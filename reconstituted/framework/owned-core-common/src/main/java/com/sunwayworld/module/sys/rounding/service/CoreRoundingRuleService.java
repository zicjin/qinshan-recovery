/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.rounding.service;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.framework.support.suspendable.service.GenericSuspendableService;
import com.sunwayworld.module.sys.rounding.bean.CoreRoundingRuleBean;
import com.sunwayworld.module.sys.rounding.bean.CoreRoundingRuleDTO;

public interface CoreRoundingRuleService
extends GenericService<CoreRoundingRuleBean, String>,
GenericChoosableService<CoreRoundingRuleBean, String>,
GenericSuspendableService<CoreRoundingRuleBean, String> {
    @Override
    public String insert(RestJsonWrapperBean var1);

    public CoreRoundingRuleDTO calculate(RestJsonWrapperBean var1);
}
