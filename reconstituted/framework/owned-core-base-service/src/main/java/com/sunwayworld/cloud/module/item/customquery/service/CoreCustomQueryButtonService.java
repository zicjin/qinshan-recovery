/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customquery.service;

import com.sunwayworld.cloud.module.item.customquery.bean.CoreCustomQueryButtonBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface CoreCustomQueryButtonService
extends GenericService<CoreCustomQueryButtonBean, Long> {
    public void addQueryButtons(RestJsonWrapperBean var1);
}
