/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.activatable.resource;

import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.domain.Activatable;
import java.io.Serializable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface GenericActivatableCloudResource<T extends Activatable<ID>, ID extends Serializable> {
    @RequestMapping(value={"/action/activate"}, method={RequestMethod.PUT})
    public void activate(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/deactivate"}, method={RequestMethod.PUT})
    public void deactivate(RestJsonWrapperBean var1);

    @RequestMapping(value={"/queries/activatable"}, method={RequestMethod.POST})
    public Pagination<T> selectActivatablePagination(RestJsonWrapperBean var1);
}
