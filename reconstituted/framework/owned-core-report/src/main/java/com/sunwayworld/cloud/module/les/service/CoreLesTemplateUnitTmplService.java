/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.service;

import com.sunwayworld.cloud.module.les.bean.CoreLesTemplateUnitTmplBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface CoreLesTemplateUnitTmplService
extends GenericService<CoreLesTemplateUnitTmplBean, Long> {
    public void insertByTemplateUnit(Long var1, RestJsonWrapperBean var2);

    public void insertByInstanceUnit(Long var1, RestJsonWrapperBean var2);
}
