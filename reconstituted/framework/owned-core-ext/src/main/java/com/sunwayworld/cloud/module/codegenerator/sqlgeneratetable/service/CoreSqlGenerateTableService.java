/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.codegenerator.sqlgeneratetable.service;

import com.sunwayworld.cloud.module.codegenerator.sqlgeneratetable.bean.CoreSqlGenerateFieldBean;
import com.sunwayworld.cloud.module.codegenerator.sqlgeneratetable.bean.CoreSqlGenerateTableBean;
import com.sunwayworld.cloud.module.codegenerator.sqlgeneratetable.bean.SqlDataDTO;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface CoreSqlGenerateTableService
extends GenericService<CoreSqlGenerateTableBean, Long> {
    public Page<CoreSqlGenerateFieldBean> selectFieldPaginationBySqlGenerateTableId(Long var1, RestJsonWrapperBean var2);

    public Long insertField(Long var1, RestJsonWrapperBean var2);

    public void deleteField(Long var1, RestJsonWrapperBean var2);

    public SqlDataDTO generateSql(RestJsonWrapperBean var1);
}
