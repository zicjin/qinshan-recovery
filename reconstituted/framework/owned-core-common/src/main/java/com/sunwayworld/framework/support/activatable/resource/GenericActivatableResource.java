/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.activatable.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.domain.Activatable;
import java.io.Serializable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface GenericActivatableResource<S extends GenericActivatableService<T, ID>, T extends Activatable<ID>, ID extends Serializable> {
    public S getService();

    @Log(value="\u6fc0\u6d3b", type=LogType.ACTIVATE)
    @RequestMapping(value={"/action/activate"}, method={RequestMethod.PUT})
    default public void activate(RestJsonWrapperBean wrapper) {
        this.getService().activate(wrapper);
    }

    @Log(value="\u5931\u6548", type=LogType.ACTIVATE)
    @RequestMapping(value={"/action/deactivate"}, method={RequestMethod.PUT})
    default public void deactivate(RestJsonWrapperBean wrapper) {
        this.getService().deactivate(wrapper);
    }

    @Log(value="\u6fc0\u6d3b\u9875\u67e5\u8be2", type=LogType.SELECT)
    @RequestMapping(value={"/queries/activatable"}, method={RequestMethod.POST})
    default public Page<T> selectActivatablePagination(RestJsonWrapperBean wrapper) {
        return this.getService().selectActivatablePagination(wrapper);
    }
}
