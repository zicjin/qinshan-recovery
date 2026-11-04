/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.choosable.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.framework.support.domain.Persistable;
import java.io.Serializable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface GenericChoosableResource<S extends GenericChoosableService<T, ID>, T extends Persistable<ID>, ID extends Serializable> {
    public S getService();

    @Log(value="\u9009\u62e9\u9875\u67e5\u8be2", type=LogType.SELECT)
    @RequestMapping(value={"/queries/choosable"}, method={RequestMethod.POST})
    default public Page<T> selectChoosablePagination(RestJsonWrapperBean wrapper) {
        return this.getService().selectChoosablePagination(wrapper);
    }
}
