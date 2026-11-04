/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.suspendable.resource;

import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.domain.Suspendable;
import com.sunwayworld.framework.support.suspendable.resource.GenericSuspendableCloudResource;
import com.sunwayworld.framework.support.suspendable.service.GenericSuspendableService;
import java.io.Serializable;

public interface AbstractGenericSuspendableResource<S extends GenericSuspendableService<T, ID>, T extends Suspendable<ID>, ID extends Serializable>
extends GenericSuspendableCloudResource<T, ID> {
    public S getService();

    @Override
    @Log(value="\u6ce8\u9500", type=LogType.SUSPEND)
    default public void suspend(RestJsonWrapperBean wrapper) {
        this.getService().suspend(wrapper);
    }

    @Override
    @Log(value="\u542f\u7528\uff08\u53d6\u6d88\u6ce8\u9500\uff09", type=LogType.SUSPEND)
    default public void unsuspend(RestJsonWrapperBean wrapper) {
        this.getService().unsuspend(wrapper);
    }

    @Override
    @Log(value="\u6ce8\u9500\u9875\u67e5\u8be2", type=LogType.SELECT)
    default public Pagination<T> selectSuspendablePagination(RestJsonWrapperBean wrapper) {
        return (Pagination)this.getService().selectSuspendablePagination(wrapper);
    }
}
