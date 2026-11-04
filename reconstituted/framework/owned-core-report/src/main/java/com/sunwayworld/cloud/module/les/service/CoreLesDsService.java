/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.service;

import com.sunwayworld.cloud.module.les.bean.CoreLesDsBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

public interface CoreLesDsService
extends GenericService<CoreLesDsBean, Long>,
GenericActivatableService<CoreLesDsBean, Long>,
GenericChoosableService<CoreLesDsBean, Long> {
    public RestValidationResultBean updateSqlIfValid(Long var1, RestJsonWrapperBean var2);
}
