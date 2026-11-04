/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.role.service;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.framework.support.suspendable.service.GenericSuspendableService;
import com.sunwayworld.framework.support.table.bean.TableHeadBean;
import com.sunwayworld.module.sys.role.bean.CoreRoleBean;
import com.sunwayworld.module.sys.role.bean.CoreRoleUserBean;
import java.util.List;

public interface CoreRoleService
extends GenericService<CoreRoleBean, Long>,
GenericSuspendableService<CoreRoleBean, Long>,
GenericChoosableService<CoreRoleBean, Long> {
    @Override
    public Long insert(RestJsonWrapperBean var1);

    public Page<CoreRoleUserBean> selectRoleUserPaginationByRole(Long var1, RestJsonWrapperBean var2);

    public void insertRoleUser(Long var1, RestJsonWrapperBean var2);

    public void deleteRoleUser(RestJsonWrapperBean var1);

    public void insertRolePermission(Long var1, String var2, RestJsonWrapperBean var3);

    public void deleteRolePermission(Long var1, String var2, RestJsonWrapperBean var3);

    public boolean isPermissionTypeAdminRole(Long var1, String var2);

    public List<TableHeadBean> selectPermissionMatrixHeadList();

    public RestValidationResultBean deleteValidate(RestJsonWrapperBean var1);
}
