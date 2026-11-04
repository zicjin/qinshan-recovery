/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.query.resource;

import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.domain.BaseData;
import com.sunwayworld.framework.support.query.resource.GenericQueryCloudResource;
import com.sunwayworld.framework.support.query.service.GenericQueryService;

public interface AbstractGenericQueryResource<S extends GenericQueryService<T>, T extends BaseData>
extends GenericQueryCloudResource<T> {
    public S getService();

    @Override
    @Log(value="\u7edf\u8ba1\u67e5\u8be2\u9875\u67e5\u8be2", type=LogType.SELECT)
    default public Pagination<T> selectQueryPagination(RestJsonWrapperBean wrapper) {
        return (Pagination)this.getService().selectQueryPagination(wrapper);
    }
}
