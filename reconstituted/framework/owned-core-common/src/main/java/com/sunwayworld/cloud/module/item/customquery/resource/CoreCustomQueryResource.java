/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customquery.resource;

import com.sunwayworld.cloud.module.item.customquery.bean.CoreCustomQueryBean;
import com.sunwayworld.cloud.module.item.customquery.bean.CoreCustomQueryButtonBean;
import com.sunwayworld.cloud.module.item.customquery.bean.CoreCustomQueryConditionBean;
import com.sunwayworld.cloud.module.item.customquery.bean.CoreCustomQueryFieldBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/item/custom-queries"})
public interface CoreCustomQueryResource
extends GenericCloudResource<CoreCustomQueryBean, Long> {
    @RequestMapping(value={"/{id}/{customDsId}/action/add-query-field"}, method={RequestMethod.POST})
    public void insertQueryFields(@PathVariable Long var1, @PathVariable Long var2);

    @RequestMapping(value={"/query-condition/source-field/{fieldId}"}, method={RequestMethod.POST})
    public void insertQueryCondition(@PathVariable Long var1);

    @RequestMapping(value={"/query-condition/source-by-field/{fieldId}"}, method={RequestMethod.DELETE})
    public void deleteQueryCondition(@PathVariable Long var1);

    @RequestMapping(value={"/fields/action/swap/{firstId}/{secondId}"}, method={RequestMethod.PUT})
    public void swapField(@PathVariable Long var1, @PathVariable Long var2);

    @RequestMapping(value={"/buttons/action/swap/{firstId}/{secondId}"}, method={RequestMethod.PUT})
    public void swapButton(@PathVariable Long var1, @PathVariable Long var2);

    @RequestMapping(value={"/conditions/action/swap/{firstId}/{secondId}"}, method={RequestMethod.PUT})
    public void swapCondition(@PathVariable Long var1, @PathVariable Long var2);

    @RequestMapping(value={"/fields/queries"}, method={RequestMethod.POST})
    public Page<CoreCustomQueryFieldBean> selectFieldPagation(RestJsonWrapperBean var1);

    @RequestMapping(value={"/buttons/queries"}, method={RequestMethod.POST})
    public Page<CoreCustomQueryButtonBean> selectButtonPagation(RestJsonWrapperBean var1);

    @RequestMapping(value={"/conditions/queries"}, method={RequestMethod.POST})
    public Page<CoreCustomQueryConditionBean> selectConditionPagation(RestJsonWrapperBean var1);

    @RequestMapping(value={"/fields/queries/choosable"}, method={RequestMethod.POST})
    public Pagination<CoreCustomQueryFieldBean> selectFieldChoosablePagination(RestJsonWrapperBean var1);

    @RequestMapping(value={"/query-buttons"}, method={RequestMethod.POST})
    public void insertQueryButtons(RestJsonWrapperBean var1);

    @RequestMapping(value={"/query-conditions"}, method={RequestMethod.POST})
    public void insertQueryConditions(RestJsonWrapperBean var1);

    @RequestMapping(value={"/query-buttons"}, method={RequestMethod.DELETE})
    public void deleteQueryButtons(RestJsonWrapperBean var1);

    @RequestMapping(value={"/query-conditions"}, method={RequestMethod.DELETE})
    public void deleteQueryConditions(RestJsonWrapperBean var1);

    @RequestMapping(value={"/{id}/action/publish"}, method={RequestMethod.POST})
    public void publish(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/action/revoke"}, method={RequestMethod.PUT})
    public void revoke(@PathVariable Long var1);

    @RequestMapping(value={"/{id}/query-fields"}, method={RequestMethod.GET})
    public List<CoreCustomQueryFieldBean> selectQueryFieldList(@PathVariable Long var1);

    @RequestMapping(value={"/{id}/query-buttons"}, method={RequestMethod.GET})
    public List<CoreCustomQueryButtonBean> selectQueryButtonList(@PathVariable Long var1);

    @RequestMapping(value={"/{id}/custom-menu-queries"}, method={RequestMethod.POST})
    public Pagination<Map<String, Object>> selectCustomMenuPagination(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/build-high-query"}, method={RequestMethod.GET})
    public List<Map<String, Object>> buildQueryCondition(@PathVariable Long var1);

    @RequestMapping(value={"/action/check-custom-sql"}, method={RequestMethod.POST})
    public RestValidationResultBean checkCustomSql(RestJsonWrapperBean var1);

    @RequestMapping(value={"/{id}/{field}/select-items"}, method={RequestMethod.GET})
    public List<Map<String, Object>> getSelectItems(@PathVariable Long var1, @PathVariable String var2);

    @RequestMapping(value={"/action/next-identity"}, method={RequestMethod.GET})
    public Long getNextIdentity();
}
