/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.org.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableResource;
import com.sunwayworld.framework.support.tree.resource.GenericTreeResource;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import com.sunwayworld.module.mdm.org.bean.CoreOrgUserBean;
import com.sunwayworld.module.mdm.org.service.CoreOrgService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/mdm/orgs"})
public interface CoreOrgResource
extends GenericResource<CoreOrgService, CoreOrgBean, String>,
GenericChoosableResource<CoreOrgService, CoreOrgBean, String>,
GenericTreeResource<CoreOrgService, CoreOrgBean, String>,
GenericSelectableResource<CoreOrgService, CoreOrgBean, String> {
    @Override
    public String insert(RestJsonWrapperBean var1);

    @RequestMapping(value={"/by-roles/{roleId}/queries"}, method={RequestMethod.POST})
    public Page<CoreOrgBean> selectRoleOrgList(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/users"}, method={RequestMethod.POST})
    public void insertOrgUser(String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/users"}, method={RequestMethod.DELETE})
    public void deleteOrgUser(String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/users/queries"}, method={RequestMethod.POST})
    public Page<CoreOrgUserBean> selectOrgUserPaginationByOrg(String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/users/queries"}, method={RequestMethod.POST})
    public Page<CoreOrgUserBean> selectOrgUserPagination(RestJsonWrapperBean var1);

    @RequestMapping(value={"/core-user-orgs/action/delete"}, method={RequestMethod.DELETE})
    public void deleteOrgUser(RestJsonWrapperBean var1);
}
