/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.org.resource.impl;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.validator.permission.annotation.ValidatePermissionWith;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import com.sunwayworld.module.mdm.org.bean.CoreOrgUserBean;
import com.sunwayworld.module.mdm.org.resource.CoreOrgResource;
import com.sunwayworld.module.mdm.org.service.CoreOrgService;
import com.sunwayworld.module.mdm.org.service.CoreOrgUserService;
import com.sunwayworld.module.mdm.org.validator.CoreOrgDeleteValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u7cfb\u7edf\u7ec4\u7ec7")
@RestController
@GikamBean
public class CoreOrgResourceImpl
implements CoreOrgResource {
    @Autowired
    private CoreOrgService orgService;
    @Autowired
    private CoreOrgUserService orgUserService;

    @Override
    public CoreOrgService getService() {
        return this.orgService;
    }

    @Override
    @Log(value="\u5f53\u524d\u89d2\u8272\u4e0b\u7684\u5df2\u5206\u914d\u5355\u4f4d\u67e5\u8be2", type=LogType.SELECT)
    public Page<CoreOrgBean> selectRoleOrgList(@PathVariable Long roleId, RestJsonWrapperBean wrapper) {
        return this.getService().selectRoleOrgList(roleId, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public String insert(RestJsonWrapperBean wrapper) {
        return this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u201c\u7ec4\u7ec7\u6210\u5458\u201d", type=LogType.INSERT)
    public void insertOrgUser(@PathVariable String id, RestJsonWrapperBean wrapper) {
        this.getService().insertUser(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u201c\u7ec4\u7ec7\u6210\u5458\u201d", type=LogType.DELETE)
    public void deleteOrgUser(@PathVariable String id, RestJsonWrapperBean wrapper) {
        this.getService().deleteUser(id, wrapper);
    }

    @Override
    @Log(value="\u901a\u8fc7\u7ec4\u7ec7\u673a\u6784\u67e5\u8be2\u201c\u7ec4\u7ec7\u6210\u5458\u201d", type=LogType.SELECT)
    public Page<CoreOrgUserBean> selectOrgUserPaginationByOrg(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return this.orgUserService.selectPaginationByOrg(id, wrapper);
    }

    @Override
    @Log(value="\u901a\u8fc7\u7ec4\u7ec7\u673a\u6784\u67e5\u8be2\u201c\u7ec4\u7ec7\u6210\u5458\u201d", type=LogType.SELECT)
    public Page<CoreOrgUserBean> selectOrgUserPagination(RestJsonWrapperBean wrapper) {
        return this.orgUserService.selectRawPagination(wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u201c\u7ec4\u7ec7\u6210\u5458\u201d", type=LogType.DELETE)
    public void deleteOrgUser(RestJsonWrapperBean wrapper) {
        this.orgUserService.delete(wrapper);
    }

    @Override
    @Log(value="\u5220\u9664", type=LogType.DELETE)
    @RequestMapping(method={RequestMethod.DELETE})
    @ValidatePermissionWith(value=CoreOrgDeleteValidator.class, table="#this.getService().getDao().getTable()")
    public void delete(RestJsonWrapperBean wrapper) {
        this.getService().delete(wrapper);
    }
}
