/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.query.resource;

import com.sunwayworld.cloud.module.item.query.bean.CoreQueryConfigBean;
import com.sunwayworld.cloud.module.item.query.bean.CoreQueryConfigColumnBean;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.resource.GenericActivatableCloudResource;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import java.util.ArrayList;
import java.util.Map;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/item/query-configs"})
public interface CoreQueryConfigResource
extends GenericCloudResource<CoreQueryConfigBean, String>,
GenericActivatableCloudResource<CoreQueryConfigBean, String> {
    @RequestMapping(value={"/{id}/query-config-columns/queries"}, method={RequestMethod.POST})
    public Pagination<CoreQueryConfigColumnBean> selectQueryConfigColumnPagination(@PathVariable String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/query-config-columns"}, method={RequestMethod.GET})
    public ArrayList<CoreQueryConfigColumnBean> selectQueryConfigColumnList(@PathVariable String var1);

    @RequestMapping(value={"/{id}/query-config-columns/action/suspend"}, method={RequestMethod.PUT})
    public void suspendConfigColumn(@PathVariable String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/query-config-columns/action/unsuspend"}, method={RequestMethod.PUT})
    public void unsuspendConfigColumn(@PathVariable String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/related-queries"}, method={RequestMethod.POST})
    public Pagination<Map<String, Object>> selectRelatedPagination(@PathVariable String var1, RestJsonWrapperBean var2);
}
