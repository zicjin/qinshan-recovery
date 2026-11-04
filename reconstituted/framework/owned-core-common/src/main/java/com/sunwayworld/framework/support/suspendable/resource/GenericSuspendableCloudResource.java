/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.suspendable.resource;

import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.domain.Suspendable;
import java.io.Serializable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface GenericSuspendableCloudResource<T extends Suspendable<ID>, ID extends Serializable> {
    @RequestMapping(value={"/action/suspend"}, method={RequestMethod.PUT})
    public void suspend(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/unsuspend"}, method={RequestMethod.PUT})
    public void unsuspend(RestJsonWrapperBean var1);

    @RequestMapping(value={"/queries/suspendable"}, method={RequestMethod.POST})
    public Pagination<T> selectSuspendablePagination(RestJsonWrapperBean var1);
}
