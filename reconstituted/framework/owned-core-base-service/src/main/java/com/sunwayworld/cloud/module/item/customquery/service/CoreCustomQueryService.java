/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customquery.service;

import com.sunwayworld.cloud.module.item.customquery.bean.CoreCustomQueryBean;
import com.sunwayworld.cloud.module.item.customquery.bean.CoreCustomQueryButtonBean;
import com.sunwayworld.cloud.module.item.customquery.bean.CoreCustomQueryConditionBean;
import com.sunwayworld.cloud.module.item.customquery.bean.CoreCustomQueryFieldBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import java.util.List;
import java.util.Map;

public interface CoreCustomQueryService
extends GenericService<CoreCustomQueryBean, Long> {
    public void insertQueryFields(Long var1, Long var2);

    public void insertQueryCondition(Long var1);

    public void deleteQueryCondition(Long var1);

    public void publish(Long var1, RestJsonWrapperBean var2);

    public List<Map<String, Object>> buildHighQuery(Long var1);

    public void revoke(Long var1);

    public Page<Map<String, Object>> selectCustomMenuPagination(Long var1, RestJsonWrapperBean var2);

    public RestValidationResultBean checkCustomSql(RestJsonWrapperBean var1);

    public List<Map<String, Object>> getSelectItems(Long var1, String var2);

    public void swapField(Long var1, Long var2);

    public void swapButton(Long var1, Long var2);

    public void swapCondition(Long var1, Long var2);

    public Page<CoreCustomQueryFieldBean> selectFieldsPagation(RestJsonWrapperBean var1);

    public Page<CoreCustomQueryButtonBean> selectButtonsPagation(RestJsonWrapperBean var1);

    public Page<CoreCustomQueryConditionBean> selectConditionsPagation(RestJsonWrapperBean var1);

    public Pagination<CoreCustomQueryFieldBean> selectChoosablePagination(RestJsonWrapperBean var1);

    public void insertQueryButtons(RestJsonWrapperBean var1);

    public void insertQueryConditions(RestJsonWrapperBean var1);

    public void deleteQueryButtons(RestJsonWrapperBean var1);

    public void deleteQueryConditions(RestJsonWrapperBean var1);

    public List<CoreCustomQueryFieldBean> selectQueryFieldList(Long var1);

    public List<CoreCustomQueryButtonBean> selectQueryButtonList(Long var1);
}
