/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.metadata.resource.impl;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.context.ColumnContext;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.metadata.resource.CoreTableResource;
import com.sunwayworld.module.sys.metadata.service.CoreTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u7cfb\u7edf\u8868")
@RestController
@GikamBean
public class CoreTableResourceImpl
implements CoreTableResource {
    @Autowired
    private CoreTableService tableService;

    @Override
    public CoreTableService getService() {
        return this.tableService;
    }

    @Override
    public Page<ColumnContext> selectTableColumnList(@PathVariable String table, RestJsonWrapperBean wrapper) {
        return this.tableService.selectTableColumnList(table, wrapper);
    }
}
