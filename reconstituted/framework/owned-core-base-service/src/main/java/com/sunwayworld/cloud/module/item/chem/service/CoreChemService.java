/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.chem.service;

import com.sunwayworld.cloud.module.item.chem.bean.CoreChemBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import org.springframework.web.bind.annotation.PathVariable;

public interface CoreChemService
extends GenericService<CoreChemBean, Long> {
    @Override
    public Long insert(RestJsonWrapperBean var1);

    public CoreChemBean selectUniqueByTargetId(@PathVariable String var1);
}
