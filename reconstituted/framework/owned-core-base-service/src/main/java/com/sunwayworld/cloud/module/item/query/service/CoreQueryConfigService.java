/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.query.service;

import com.sunwayworld.cloud.module.item.query.bean.CoreQueryConfigBean;
import com.sunwayworld.cloud.module.item.query.bean.CoreQueryConfigColumnBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import java.util.List;
import java.util.Map;

public interface CoreQueryConfigService
extends GenericService<CoreQueryConfigBean, String>,
GenericActivatableService<CoreQueryConfigBean, String> {
    @Override
    public String insert(RestJsonWrapperBean var1);

    public Page<CoreQueryConfigColumnBean> selectQueryConfigColumnPagination(String var1, RestJsonWrapperBean var2);

    public List<CoreQueryConfigColumnBean> selectQueryConfigColumnList(String var1);

    public void suspendConfigColumn(String var1, RestJsonWrapperBean var2);

    public void unsuspendConfigColumn(String var1, RestJsonWrapperBean var2);

    public Page<Map<String, Object>> selectRelatedPagination(String var1, RestJsonWrapperBean var2);
}
