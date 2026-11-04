/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.office.service;

import com.sunwayworld.cloud.module.office.bean.CoreOfficeToolbarBean;
import com.sunwayworld.cloud.module.office.bean.CoreOfficeToolbarDTO;
import com.sunwayworld.cloud.module.office.bean.CoreOfficeToolbarLineBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.suspendable.service.GenericSuspendableService;

public interface CoreOfficeToolbarService
extends GenericService<CoreOfficeToolbarBean, Long>,
GenericSuspendableService<CoreOfficeToolbarBean, Long> {
    public Page<CoreOfficeToolbarDTO> selectToolbarPagination(String var1, RestJsonWrapperBean var2);

    public void deleteLine(RestJsonWrapperBean var1);

    public Long insertLine(Long var1, RestJsonWrapperBean var2);

    public Page<CoreOfficeToolbarLineBean> selectLinePagination(Long var1, RestJsonWrapperBean var2);

    public void suspendLine(RestJsonWrapperBean var1);

    public void unsuspendLine(RestJsonWrapperBean var1);
}
