/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.launchnewproject.service;

import com.sunwayworld.cloud.module.qms.launchnewproject.bean.CoreLaunchNewProjectBean;
import com.sunwayworld.cloud.module.qms.launchnewproject.bean.CoreLaunchNewProjectItemBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import java.util.List;

public interface CoreLaunchNewProjectService
extends GenericService<CoreLaunchNewProjectBean, Long>,
GenericAuditableService<CoreLaunchNewProjectBean, Long>,
GenericChoosableService<CoreLaunchNewProjectBean, Long> {
    public Page<CoreLaunchNewProjectItemBean> selectNewProjectItemPaginationByLaunchNewProjectId(Long var1, RestJsonWrapperBean var2);

    public List<Long> insertNewProjectItem(Long var1, RestJsonWrapperBean var2);

    public void deleteNewProjectItem(Long var1, RestJsonWrapperBean var2);
}
