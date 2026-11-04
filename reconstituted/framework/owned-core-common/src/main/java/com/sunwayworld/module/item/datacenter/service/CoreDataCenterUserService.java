/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.datacenter.service;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.item.datacenter.bean.CoreDataCenterRoleBean;
import com.sunwayworld.module.item.datacenter.bean.CoreDataCenterUserBean;
import com.sunwayworld.module.sys.role.bean.CoreRoleUserBean;
import java.util.List;

public interface CoreDataCenterUserService
extends GenericService<CoreDataCenterUserBean, Long> {
    public void syncAfterDataCenterRoleModify(List<CoreDataCenterRoleBean> var1, String var2);

    public void syncAfterRoleUserModify(List<CoreRoleUserBean> var1, String var2);

    public void sync(RestJsonWrapperBean var1);
}
