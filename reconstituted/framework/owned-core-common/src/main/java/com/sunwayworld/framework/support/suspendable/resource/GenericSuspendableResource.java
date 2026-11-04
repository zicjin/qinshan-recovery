/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.suspendable.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.domain.Suspendable;
import com.sunwayworld.framework.support.suspendable.service.GenericSuspendableService;
import java.io.Serializable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface GenericSuspendableResource<S extends GenericSuspendableService<T, ID>, T extends Suspendable<ID>, ID extends Serializable> {
    public S getService();

    @Log(value="\u6ce8\u9500", type=LogType.SUSPEND)
    @RequestMapping(value={"/action/suspend"}, method={RequestMethod.PUT})
    default public void suspend(RestJsonWrapperBean wrapper) {
        this.getService().suspend(wrapper);
    }

    @Log(value="\u542f\u7528\uff08\u53d6\u6d88\u6ce8\u9500\uff09", type=LogType.SUSPEND)
    @RequestMapping(value={"/action/unsuspend"}, method={RequestMethod.PUT})
    default public void unsuspend(RestJsonWrapperBean wrapper) {
        this.getService().unsuspend(wrapper);
    }

    @Log(value="\u6ce8\u9500\u9875\u67e5\u8be2", type=LogType.SELECT)
    @RequestMapping(value={"/queries/suspendable"}, method={RequestMethod.POST})
    default public Page<T> selectSuspendablePagination(RestJsonWrapperBean wrapper) {
        return this.getService().selectSuspendablePagination(wrapper);
    }
}
