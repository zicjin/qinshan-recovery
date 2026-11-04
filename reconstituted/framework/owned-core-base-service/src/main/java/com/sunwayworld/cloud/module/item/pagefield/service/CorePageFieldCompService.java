/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.pagefield.service;

import com.sunwayworld.cloud.module.item.pagefield.bean.CorePageFieldCompBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface CorePageFieldCompService
extends GenericService<CorePageFieldCompBean, Long> {
    public Long insert(Long var1, RestJsonWrapperBean var2);
}
