/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.codegenerator.sqlgeneratetable.resource;

import com.sunwayworld.cloud.module.codegenerator.sqlgeneratetable.bean.CoreSqlGenerateFieldBean;
import com.sunwayworld.cloud.module.codegenerator.sqlgeneratetable.bean.CoreSqlGenerateTableBean;
import com.sunwayworld.cloud.module.codegenerator.sqlgeneratetable.bean.SqlDataDTO;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/codegenerator/sql-generate-tables"})
public interface CoreSqlGenerateTableResource
extends GenericCloudResource<CoreSqlGenerateTableBean, Long> {
    @RequestMapping(value={"/{id}/fields/queries"}, method={RequestMethod.POST})
    public Pagination<CoreSqlGenerateFieldBean> selectFieldPaginationBySqlGenerateTableId(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/fields"}, method={RequestMethod.POST})
    public Long insertField(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/fields"}, method={RequestMethod.DELETE})
    public void deleteField(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/action/generate-sql"}, method={RequestMethod.POST})
    public SqlDataDTO generateSql(RestJsonWrapperBean var1);
}
