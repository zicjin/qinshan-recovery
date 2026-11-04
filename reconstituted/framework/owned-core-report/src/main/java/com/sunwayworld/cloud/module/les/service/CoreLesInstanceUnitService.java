/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.service;

import com.sunwayworld.cloud.module.les.bean.CoreLesInstanceUnitBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import java.util.List;

public interface CoreLesInstanceUnitService
extends GenericService<CoreLesInstanceUnitBean, Long> {
    public void dedentSection(Long var1, RestJsonWrapperBean var2);

    public void indentSection(Long var1, Long var2, RestJsonWrapperBean var3);

    public void refreshUnit(List<CoreLesInstanceUnitBean> var1);

    public void refreshUnit(Long var1, RestJsonWrapperBean var2);
}
