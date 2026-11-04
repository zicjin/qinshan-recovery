/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.tenant.service;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.admin.tenant.bean.CoreAdminTenantBean;
import com.sunwayworld.module.admin.tenant.bean.CoreAdminTenantDTO;
import com.sunwayworld.module.admin.tenant.bean.CoreAdminTenantUserBean;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import java.util.List;

public interface CoreAdminTenantService
extends GenericService<CoreAdminTenantBean, String>,
GenericActivatableService<CoreAdminTenantBean, String> {
    public static final String SYNC_LIST_CACHE_NAME = "TENANT_SYNC";

    public boolean isSynchronizing(String var1);

    public void syncUser(String var1, CoreUserBean var2, CoreUserBean var3);

    public void insertUsers(String var1, RestJsonWrapperBean var2);

    public void deleteUsers(String var1, RestJsonWrapperBean var2);

    public Page<CoreAdminTenantUserBean> selectUserPagination(String var1, RestJsonWrapperBean var2);

    public List<CoreAdminTenantDTO> selectLoginTenantList(RestJsonWrapperBean var1);
}
