/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.question.service;

import com.sunwayworld.cloud.module.train.question.bean.CoreQuestionBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.suspendable.service.GenericSuspendableService;
import java.util.List;

public interface CoreQuestionService
extends GenericService<CoreQuestionBean, Long>,
GenericActivatableService<CoreQuestionBean, Long>,
GenericSuspendableService<CoreQuestionBean, Long> {
    public List<Long> clone(RestJsonWrapperBean var1);
}
