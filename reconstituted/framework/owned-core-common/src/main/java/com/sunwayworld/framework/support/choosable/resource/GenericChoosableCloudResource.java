/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.choosable.resource;

import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.domain.Persistable;
import java.io.Serializable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface GenericChoosableCloudResource<T extends Persistable<ID>, ID extends Serializable> {
    @RequestMapping(value={"/queries/choosable"}, method={RequestMethod.POST})
    public Pagination<T> selectChoosablePagination(RestJsonWrapperBean var1);
}
