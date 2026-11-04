/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.email.resource;

import com.sunwayworld.cloud.module.email.bean.CoreEmailConfigBean;
import com.sunwayworld.cloud.module.email.bean.CoreEmailConfigCcRoleBean;
import com.sunwayworld.cloud.module.email.bean.CoreEmailConfigCcUserBean;
import com.sunwayworld.cloud.module.email.bean.CoreEmailConfigRecRoleBean;
import com.sunwayworld.cloud.module.email.bean.CoreEmailConfigReceipentBean;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.suspendable.resource.GenericSuspendableCloudResource;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface CoreEmailConfigResource
extends GenericCloudResource<CoreEmailConfigBean, String>,
GenericSuspendableCloudResource<CoreEmailConfigBean, String> {
    @RequestMapping(value={"/{id}/email-config-receipents"}, method={RequestMethod.POST})
    public void insertReceiptent(@PathVariable String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/email-config-roles"}, method={RequestMethod.POST})
    public void insertRole(@PathVariable String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/email-config-receipents"}, method={RequestMethod.DELETE})
    public void deleteReceiptent(@PathVariable String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/email-config-roles"}, method={RequestMethod.DELETE})
    public void deleteRole(@PathVariable String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/email-config-receipents/queries"}, method={RequestMethod.POST})
    public Pagination<CoreEmailConfigReceipentBean> selectReceipentByConfig(@PathVariable String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/email-config-roles/queries"}, method={RequestMethod.POST})
    public Pagination<CoreEmailConfigRecRoleBean> selectRoleByConfig(@PathVariable String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/email-config-cc-users"}, method={RequestMethod.POST})
    public List<Long> insertCcUser(@PathVariable String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/email-config-cc-users"}, method={RequestMethod.DELETE})
    public void deleteCcUser(@PathVariable String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/email-config-cc-users/queries"}, method={RequestMethod.POST})
    public Pagination<CoreEmailConfigCcUserBean> selectCcUserByConfig(@PathVariable String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/email-config-cc-roles"}, method={RequestMethod.POST})
    public List<Long> insertCcRole(@PathVariable String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/email-config-cc-roles"}, method={RequestMethod.DELETE})
    public void deleteCcRole(@PathVariable String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/email-config-cc-roles/queries"}, method={RequestMethod.POST})
    public Pagination<CoreEmailConfigCcRoleBean> selectCcRoleByConfig(@PathVariable String var1, RestJsonWrapperBean var2);
}
