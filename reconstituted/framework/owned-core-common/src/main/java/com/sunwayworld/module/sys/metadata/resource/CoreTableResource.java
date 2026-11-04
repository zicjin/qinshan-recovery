/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.metadata.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.context.ColumnContext;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;
import com.sunwayworld.module.sys.metadata.bean.CoreTableBean;
import com.sunwayworld.module.sys.metadata.service.CoreTableService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/sys/tables"})
public interface CoreTableResource
extends GenericResource<CoreTableService, CoreTableBean, String>,
GenericChoosableResource<CoreTableService, CoreTableBean, String> {
    @RequestMapping(value={"/{table}/columns/queries/choosable"}, method={RequestMethod.POST})
    public Page<ColumnContext> selectTableColumnList(String var1, RestJsonWrapperBean var2);
}
