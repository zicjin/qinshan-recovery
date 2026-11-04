/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customquery.service;

import com.sunwayworld.cloud.module.item.customquery.bean.CoreCustomQueryConditionBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface CoreCustomQueryConditionService
extends GenericService<CoreCustomQueryConditionBean, Long> {
    public void addQueryConditions(RestJsonWrapperBean var1);

    public void delQueryConditions(RestJsonWrapperBean var1);
}
