/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.datacenter.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableResource;
import com.sunwayworld.framework.support.tree.resource.GenericTreeResource;
import com.sunwayworld.module.item.datacenter.bean.CoreDataCenterBean;
import com.sunwayworld.module.item.datacenter.bean.CoreDataCenterDTO;
import com.sunwayworld.module.item.datacenter.bean.CoreDataCenterParamBean;
import com.sunwayworld.module.item.datacenter.bean.CoreDataCenterRoleBean;
import com.sunwayworld.module.item.datacenter.bean.CoreDataCenterUserBean;
import com.sunwayworld.module.item.datacenter.service.CoreDataCenterService;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/item/data-centers"})
public interface CoreDataCenterResource
extends GenericResource<CoreDataCenterService, CoreDataCenterBean, Long>,
GenericTreeResource<CoreDataCenterService, CoreDataCenterBean, Long>,
GenericSelectableResource<CoreDataCenterService, CoreDataCenterBean, Long> {
    public Page<CoreDataCenterRoleBean> selectRolePaginationByDataCenterId(Long var1, RestJsonWrapperBean var2);

    public Long insertRole(Long var1, RestJsonWrapperBean var2);

    public void deleteRole(Long var1, RestJsonWrapperBean var2);

    public void sync(RestJsonWrapperBean var1);

    public List<CoreDataCenterBean> selectCurrentUserDataCenter();

    public void drag(RestJsonWrapperBean var1);

    public Page<CoreDataCenterDTO> selectReportTree(RestJsonWrapperBean var1);

    public Long insertParam(Long var1, RestJsonWrapperBean var2);

    public void deleteParam(Long var1, RestJsonWrapperBean var2);

    public Page<CoreDataCenterParamBean> selectParamPaginationByDataCenterId(Long var1, RestJsonWrapperBean var2);

    public Page<CoreDataCenterUserBean> selectCurrentUserPagination(RestJsonWrapperBean var1);

    public void syncUserDataCenterData(RestJsonWrapperBean var1);

    public void swapUser(String var1, Long var2, Long var3);
}
