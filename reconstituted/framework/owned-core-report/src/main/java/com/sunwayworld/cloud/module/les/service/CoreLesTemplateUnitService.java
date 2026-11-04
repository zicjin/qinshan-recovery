/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.service;

import com.sunwayworld.cloud.module.les.bean.CoreLesTemplateUnitBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericImportService;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface CoreLesTemplateUnitService
extends GenericService<CoreLesTemplateUnitBean, Long>,
GenericImportService<CoreLesTemplateUnitBean, Long> {
    public void dedentSection(Long var1, RestJsonWrapperBean var2);

    public void indentSection(Long var1, Long var2, RestJsonWrapperBean var3);
}
