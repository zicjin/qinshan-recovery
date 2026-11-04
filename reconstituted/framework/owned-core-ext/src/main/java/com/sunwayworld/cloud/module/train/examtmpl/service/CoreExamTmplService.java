/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.examtmpl.service;

import com.sunwayworld.cloud.module.train.examtmpl.bean.CoreExamTmplBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import java.util.List;

public interface CoreExamTmplService
extends GenericService<CoreExamTmplBean, Long>,
GenericChoosableService<CoreExamTmplBean, Long>,
GenericActivatableService<CoreExamTmplBean, Long> {
    public List<Long> clone(RestJsonWrapperBean var1);

    public RestValidationResultBean deleteValidate(RestJsonWrapperBean var1);
}
