/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.role.service;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.matrix.service.GenericMatrixService;
import com.sunwayworld.module.sys.role.bean.CoreRoleUserBean;
import java.util.List;

public interface CoreRoleUserService
extends GenericService<CoreRoleUserBean, Long>,
GenericMatrixService {
    @Override
    public Long insert(RestJsonWrapperBean var1);

    public Long insertByUserId(String var1, List<Long> var2);

    public void deleteByUserId(String var1, List<Long> var2);

    public void deleteByUserId(String var1);

    public void deleteByRoleId(Long var1);

    public List<CoreRoleUserBean> selectByUserId(String var1);

    public List<String> selectUserList(Long var1);

    public Page<CoreRoleUserBean> selectPaginationByRole(Long var1, RestJsonWrapperBean var2);

    public void setDefault(String var1, RestJsonWrapperBean var2);
}
