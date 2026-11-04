/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.user.resource.impl;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.mdm.user.bean.CoreUserPermissionBean;
import com.sunwayworld.module.mdm.user.resource.CoreUserPermissionResource;
import com.sunwayworld.module.mdm.user.service.CoreUserPermissionService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u7528\u6237\u6743\u9650")
@RestController
@GikamBean
public class CoreUserPermissionResourceImpl
implements CoreUserPermissionResource {
    @Autowired
    private CoreUserPermissionService userPermissionService;

    @Override
    public CoreUserPermissionService getService() {
        return this.userPermissionService;
    }

    @Override
    @Log(value="\u67e5\u8be2\u6307\u5b9a\u7c7b\u578b\u4e0b\u7684\u6743\u9650", type=LogType.SELECT)
    @RequestMapping(value={"/permission-types/{permissionTypeId}/queries"}, method={RequestMethod.POST})
    public Page<CoreUserPermissionBean> selectPaginationByPermissionType(@PathVariable String permissionTypeId, RestJsonWrapperBean wrapper) {
        return this.getService().selectPaginationByPermissionType(permissionTypeId, wrapper);
    }

    @Override
    @Log(value="\u83b7\u53d6\u5f53\u524d\u767b\u5f55\u4eba\u5458\u7684\u89d2\u8272", type=LogType.SELECT)
    public List<CoreUserPermissionBean> selectLoginRoleList(RestJsonWrapperBean wrapper) {
        return this.getService().selectLoginPermissionList(wrapper, "T_CORE_ROLE");
    }

    @Override
    @Log(value="\u83b7\u53d6\u5f53\u524d\u767b\u5f55\u4eba\u5458\u7684\u90e8\u95e8", type=LogType.SELECT)
    public List<CoreUserPermissionBean> selectLoginOrgList(RestJsonWrapperBean wrapper) {
        return this.getService().selectLoginPermissionList(wrapper, "T_CORE_ORG");
    }
}
