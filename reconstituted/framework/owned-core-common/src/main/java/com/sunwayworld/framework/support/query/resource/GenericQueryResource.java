/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.query.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.domain.BaseData;
import com.sunwayworld.framework.support.query.service.GenericQueryService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface GenericQueryResource<S extends GenericQueryService<T>, T extends BaseData> {
    public S getService();

    @Log(value="\u7edf\u8ba1\u67e5\u8be2\u9875\u67e5\u8be2", type=LogType.SELECT)
    @RequestMapping(value={"/static-queries/searchable"}, method={RequestMethod.POST})
    default public Page<T> selectQueryPagination(RestJsonWrapperBean wrapper) {
        return this.getService().selectQueryPagination(wrapper);
    }
}
