/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.choosable.service;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.domain.Activatable;
import com.sunwayworld.framework.support.domain.Auditable;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.support.domain.Suspendable;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessStatus;
import java.io.Serializable;

public interface GenericChoosableService<T extends Persistable<ID>, ID extends Serializable>
extends GenericService<T, ID> {
    default public Page<T> selectChoosablePagination(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        parameter.setOrgAuthority();
        parameter.setChoosableQueries();
        if (Auditable.class.isAssignableFrom(this.getDao().getType())) {
            parameter.setProcessStatus("PROCESSSTATUS", ProcessStatus.DONE);
        }
        if (Suspendable.class.isAssignableFrom(this.getDao().getType())) {
            parameter.setSuspendedFlag("0");
        }
        if (Activatable.class.isAssignableFrom(this.getDao().getType())) {
            parameter.setActivatedFlag("1");
        }
        parameter.putAll(wrapper.getExtFilter());
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return this.selectPagination(parameter, rowBounds);
    }
}
