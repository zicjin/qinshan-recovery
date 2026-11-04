/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.org.service;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;
import com.sunwayworld.framework.support.tree.service.GenericTreeService;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import java.util.List;

public interface CoreOrgService
extends GenericService<CoreOrgBean, String>,
GenericChoosableService<CoreOrgBean, String>,
GenericTreeService<CoreOrgBean, String>,
GenericSelectableService<CoreOrgBean, String> {
    public Page<CoreOrgBean> selectRoleOrgList(Long var1, RestJsonWrapperBean var2);

    @Override
    public String insert(RestJsonWrapperBean var1);

    public void insertUser(String var1, RestJsonWrapperBean var2);

    public void deleteUser(String var1, RestJsonWrapperBean var2);

    public void insertOrgUser(String var1, RestJsonWrapperBean var2);

    public List<CoreOrgBean> selectChildOrgList(String var1);

    public List<CoreOrgBean> selectParentOrgList(String var1);
}
