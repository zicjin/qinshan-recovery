/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.train.service;

import com.sunwayworld.cloud.module.train.train.bean.CoreTrainUserBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface CoreTrainUserService
extends GenericService<CoreTrainUserBean, Long> {
    public void sign(Long var1, RestJsonWrapperBean var2);
}
