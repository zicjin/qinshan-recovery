/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.code.service;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;
import com.sunwayworld.framework.support.suspendable.service.GenericSuspendableService;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;

public interface CoreCodeService
extends GenericService<CoreCodeBean, Long>,
GenericSuspendableService<CoreCodeBean, Long>,
GenericSelectableService<CoreCodeBean, Long>,
GenericChoosableService<CoreCodeBean, Long> {
    @Override
    public Long insert(RestJsonWrapperBean var1);
}
