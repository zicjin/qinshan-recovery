/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.resource;

import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.bean.CoreCodeGenerateConfigBean;
import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.bean.CoreCodeGenerateConfigFieldBean;
import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.bean.CoreCodeGenerateConfigTableBean;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/codegenerator/code-generate-configs"})
public interface CoreCodeGenerateConfigResource
extends GenericCloudResource<CoreCodeGenerateConfigBean, String> {
    @RequestMapping(value={"/{id}/action/code-generate"}, method={RequestMethod.POST})
    public String codeGenerate(@PathVariable String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/action/copy"}, method={RequestMethod.POST})
    public String copy(@PathVariable String var1);

    @RequestMapping(value={"/{id}/tables"}, method={RequestMethod.POST})
    public Long insertTable(@PathVariable String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/tables"}, method={RequestMethod.DELETE})
    public void deleteTable(RestJsonWrapperBean var1);

    @RequestMapping(value={"/{id}/tables/queries"}, method={RequestMethod.POST})
    public Pagination<CoreCodeGenerateConfigTableBean> selectTablePaginationByConfigId(@PathVariable String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/fields"}, method={RequestMethod.POST})
    public Long insertField(RestJsonWrapperBean var1);

    @RequestMapping(value={"/fields"}, method={RequestMethod.DELETE})
    public void deleteField(RestJsonWrapperBean var1);

    @RequestMapping(value={"/fields/queries"}, method={RequestMethod.POST})
    public Pagination<CoreCodeGenerateConfigFieldBean> selectFieldPagination(RestJsonWrapperBean var1);

    @RequestMapping(value={"/fields/action/swap/{firstId}/{secondId}"}, method={RequestMethod.PUT})
    public void swapField(@PathVariable Long var1, @PathVariable Long var2);
}
