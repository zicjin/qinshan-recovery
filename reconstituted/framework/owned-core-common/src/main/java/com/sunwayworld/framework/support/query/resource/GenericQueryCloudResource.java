/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.query.resource;

import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.domain.BaseData;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface GenericQueryCloudResource<T extends BaseData> {
    @RequestMapping(value={"/static-queries/searchable"}, method={RequestMethod.POST})
    public Pagination<T> selectQueryPagination(RestJsonWrapperBean var1);
}
