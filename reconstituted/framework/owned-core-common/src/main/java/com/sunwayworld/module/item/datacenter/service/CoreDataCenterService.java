/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.datacenter.service;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;
import com.sunwayworld.framework.support.tree.service.GenericTreeService;
import com.sunwayworld.module.item.datacenter.bean.CoreDataCenterBean;
import com.sunwayworld.module.item.datacenter.bean.CoreDataCenterDTO;
import com.sunwayworld.module.item.datacenter.bean.CoreDataCenterParamBean;
import com.sunwayworld.module.item.datacenter.bean.CoreDataCenterRoleBean;
import com.sunwayworld.module.item.datacenter.bean.CoreDataCenterUserBean;
import java.util.List;

public interface CoreDataCenterService
extends GenericService<CoreDataCenterBean, Long>,
GenericTreeService<CoreDataCenterBean, Long>,
GenericSelectableService<CoreDataCenterBean, Long> {
    public Page<CoreDataCenterRoleBean> selectRolePaginationByDataCenterId(Long var1, RestJsonWrapperBean var2);

    public Long insertRole(Long var1, RestJsonWrapperBean var2);

    public void deleteRole(Long var1, RestJsonWrapperBean var2);

    public void sync(RestJsonWrapperBean var1);

    public void syncDataCenterData();

    public List<CoreDataCenterBean> selectCurrentUserDataCenter();

    public void drag(RestJsonWrapperBean var1);

    public Page<CoreDataCenterDTO> selectReportTree(RestJsonWrapperBean var1);

    public Long insertParam(Long var1, RestJsonWrapperBean var2);

    public void deleteParam(Long var1, RestJsonWrapperBean var2);

    public Page<CoreDataCenterParamBean> selectParamPaginationByDataCenterId(Long var1, RestJsonWrapperBean var2);

    public void syncUserDataCenterData(RestJsonWrapperBean var1);

    public Page<CoreDataCenterUserBean> selectCurrentUserPagination(RestJsonWrapperBean var1);

    public void swapUser(String var1, Long var2, Long var3);
}
