/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.topology.service;

import com.sunwayworld.cloud.module.topology.bean.CoreTopologyBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface CoreTopologyService
extends GenericService<CoreTopologyBean, Long> {
    @Override
    public Long insert(RestJsonWrapperBean var1);

    public String selectRevisedJsonByCode(String var1);
}
