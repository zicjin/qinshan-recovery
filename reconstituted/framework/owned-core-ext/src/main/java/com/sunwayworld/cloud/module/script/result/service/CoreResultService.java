/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.script.result.service;

import com.sunwayworld.cloud.module.script.result.bean.CoreResultBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface CoreResultService
extends GenericService<CoreResultBean, Long> {
    public void compute(RestJsonWrapperBean var1);
}
