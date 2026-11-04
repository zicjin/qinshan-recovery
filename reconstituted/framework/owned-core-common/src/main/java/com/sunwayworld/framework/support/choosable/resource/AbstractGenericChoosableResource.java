/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.choosable.resource;

import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableCloudResource;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.framework.support.domain.Persistable;
import java.io.Serializable;

public interface AbstractGenericChoosableResource<S extends GenericChoosableService<T, ID>, T extends Persistable<ID>, ID extends Serializable>
extends GenericChoosableCloudResource<T, ID> {
    public S getService();

    @Override
    @Log(value="\u9009\u62e9\u9875\u67e5\u8be2", type=LogType.SELECT)
    default public Pagination<T> selectChoosablePagination(RestJsonWrapperBean wrapper) {
        return (Pagination)this.getService().selectChoosablePagination(wrapper);
    }
}
