/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.org.service;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.mdm.org.bean.CoreOrgUserBean;
import java.util.List;

public interface CoreOrgUserService
extends GenericService<CoreOrgUserBean, Long> {
    public Page<CoreOrgUserBean> selectPaginationByOrg(String var1, RestJsonWrapperBean var2);

    default public Page<CoreOrgUserBean> selectPaginationByOrg(String orgId) {
        return this.selectPaginationByOrg(orgId, null);
    }

    public void deleteByUserId(String var1, String ... var2);

    public void insertByUserId(String var1, List<CoreOrgUserBean> var2);
}
