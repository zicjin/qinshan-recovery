/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mytask.service;

import com.sunwayworld.cloud.module.mytask.bean.CoreMyTaskBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface CoreMyTaskService
extends GenericService<CoreMyTaskBean, Long> {
    @Override
    public Long insert(RestJsonWrapperBean var1);

    public Long insert(CoreMyTaskBean var1);
}
