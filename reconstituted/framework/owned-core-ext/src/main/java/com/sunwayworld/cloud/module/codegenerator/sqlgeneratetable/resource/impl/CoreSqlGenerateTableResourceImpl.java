/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.codegenerator.sqlgeneratetable.resource.impl;

import com.sunwayworld.cloud.module.codegenerator.sqlgeneratetable.bean.CoreSqlGenerateFieldBean;
import com.sunwayworld.cloud.module.codegenerator.sqlgeneratetable.bean.CoreSqlGenerateTableBean;
import com.sunwayworld.cloud.module.codegenerator.sqlgeneratetable.bean.SqlDataDTO;
import com.sunwayworld.cloud.module.codegenerator.sqlgeneratetable.resource.CoreSqlGenerateTableResource;
import com.sunwayworld.cloud.module.codegenerator.sqlgeneratetable.service.CoreSqlGenerateTableService;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u751f\u6210sql\u8868")
@RestController
@GikamBean
public class CoreSqlGenerateTableResourceImpl
implements CoreSqlGenerateTableResource,
AbstractGenericResource<CoreSqlGenerateTableService, CoreSqlGenerateTableBean, Long> {
    @Autowired
    private CoreSqlGenerateTableService sqlTableService;

    @Override
    public CoreSqlGenerateTableService getService() {
        return this.sqlTableService;
    }

    @Override
    @Log(value="\u65b0\u589e\u751f\u6210sql\u8868", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u751f\u6210sql", type=LogType.REST_CONTROLLER)
    public SqlDataDTO generateSql(RestJsonWrapperBean wrapper) {
        return this.getService().generateSql(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u751f\u6210sql\u8868ID\u67e5\u8be2\u751f\u6210sql\u5b57\u6bb5", type=LogType.SELECT)
    public Pagination<CoreSqlGenerateFieldBean> selectFieldPaginationBySqlGenerateTableId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return (Pagination)this.getService().selectFieldPaginationBySqlGenerateTableId(id, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u751f\u6210sql\u5b57\u6bb5", type=LogType.INSERT)
    public Long insertField(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().insertField(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u751f\u6210sql\u5b57\u6bb5", type=LogType.DELETE)
    public void deleteField(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().deleteField(id, wrapper);
    }
}
