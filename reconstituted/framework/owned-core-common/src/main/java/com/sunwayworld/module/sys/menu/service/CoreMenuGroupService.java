/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.service;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;
import com.sunwayworld.framework.support.suspendable.service.GenericSuspendableService;
import com.sunwayworld.module.sys.menu.bean.CoreMenuGroupBean;
import com.sunwayworld.module.sys.menu.bean.CoreMenuGroupLineRepresentation;

public interface CoreMenuGroupService
extends GenericService<CoreMenuGroupBean, String>,
GenericSuspendableService<CoreMenuGroupBean, String>,
GenericChoosableService<CoreMenuGroupBean, String>,
GenericSelectableService<CoreMenuGroupBean, String> {
    @Override
    public String insert(RestJsonWrapperBean var1);

    public void insertLines(String var1, RestJsonWrapperBean var2);

    public void deleteLines(String var1, RestJsonWrapperBean var2);

    public Page<CoreMenuGroupLineRepresentation> selectLineTreePagination(String var1, RestJsonWrapperBean var2);
}
