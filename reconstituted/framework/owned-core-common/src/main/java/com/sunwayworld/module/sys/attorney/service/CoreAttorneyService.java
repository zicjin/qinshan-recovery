/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.attorney.service;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.suspendable.service.GenericSuspendableService;
import com.sunwayworld.module.sys.attorney.bean.CoreAttorneyBean;
import com.sunwayworld.module.sys.attorney.bean.CoreAttorneyDTO;
import com.sunwayworld.module.sys.attorney.bean.CoreAttorneyMenuBean;
import java.util.List;

public interface CoreAttorneyService
extends GenericService<CoreAttorneyBean, Long>,
GenericActivatableService<CoreAttorneyBean, Long>,
GenericSuspendableService<CoreAttorneyBean, Long> {
    public Page<CoreAttorneyMenuBean> selectAttorneyMenuPagination(Long var1, RestJsonWrapperBean var2);

    public void insertAttorneyMenus(Long var1, RestJsonWrapperBean var2);

    public void deleteAttorneyMenus(Long var1, RestJsonWrapperBean var2);

    public LoginUser makeTrustorLoginUser(String var1);

    public void switchTrustor(String var1);

    public void switchTrustee();

    public List<String> selectEntrustedMenuIdList();

    public List<CoreAttorneyDTO> selectAttorneyTodoList();
}
