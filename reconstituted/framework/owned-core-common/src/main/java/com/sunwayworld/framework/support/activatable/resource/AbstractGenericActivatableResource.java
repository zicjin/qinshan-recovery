/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.activatable.resource;

import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.resource.GenericActivatableCloudResource;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.domain.Activatable;
import java.io.Serializable;

public interface AbstractGenericActivatableResource<S extends GenericActivatableService<T, ID>, T extends Activatable<ID>, ID extends Serializable>
extends GenericActivatableCloudResource<T, ID> {
    public S getService();

    @Override
    @Log(value="\u6fc0\u6d3b", type=LogType.ACTIVATE)
    default public void activate(RestJsonWrapperBean wrapper) {
        this.getService().activate(wrapper);
    }

    @Override
    @Log(value="\u5931\u6548", type=LogType.ACTIVATE)
    default public void deactivate(RestJsonWrapperBean wrapper) {
        this.getService().deactivate(wrapper);
    }

    @Override
    @Log(value="\u6fc0\u6d3b\u9875\u67e5\u8be2", type=LogType.SELECT)
    default public Pagination<T> selectActivatablePagination(RestJsonWrapperBean wrapper) {
        return (Pagination)this.getService().selectActivatablePagination(wrapper);
    }
}
