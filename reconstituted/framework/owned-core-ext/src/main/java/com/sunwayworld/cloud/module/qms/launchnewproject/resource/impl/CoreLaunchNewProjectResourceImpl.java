/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.launchnewproject.resource.impl;

import com.sunwayworld.cloud.module.qms.launchnewproject.bean.CoreLaunchNewProjectBean;
import com.sunwayworld.cloud.module.qms.launchnewproject.bean.CoreLaunchNewProjectItemBean;
import com.sunwayworld.cloud.module.qms.launchnewproject.resource.CoreLaunchNewProjectResource;
import com.sunwayworld.cloud.module.qms.launchnewproject.service.CoreLaunchNewProjectService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u65b0\u9879\u76ee\u5f00\u5c55")
@RestController
@GikamBean
public class CoreLaunchNewProjectResourceImpl
implements CoreLaunchNewProjectResource,
AbstractGenericResource<CoreLaunchNewProjectService, CoreLaunchNewProjectBean, Long>,
GenericAuditableResource<CoreLaunchNewProjectService, CoreLaunchNewProjectBean, Long>,
GenericChoosableResource<CoreLaunchNewProjectService, CoreLaunchNewProjectBean, Long> {
    @Autowired
    private CoreLaunchNewProjectService coreLaunchNewProjectService;

    @Override
    public CoreLaunchNewProjectService getService() {
        return this.coreLaunchNewProjectService;
    }

    @Override
    @Log(value="\u65b0\u589e\u65b0\u9879\u76ee\u5f00\u5c55", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u65b0\u9879\u76ee\u5f00\u5c55ID\u67e5\u8be2\u65b0\u9879\u76ee\u5f00\u5c55\u5b9e\u65bd", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/new-project-items/queries"}, method={RequestMethod.POST})
    public Page<CoreLaunchNewProjectItemBean> selectNewProjectItemPaginationByLaunchNewProjectId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectNewProjectItemPaginationByLaunchNewProjectId(id, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u65b0\u9879\u76ee\u5f00\u5c55\u5b9e\u65bd", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/new-project-items"}, method={RequestMethod.POST})
    public List<Long> insertNewProjectItem(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().insertNewProjectItem(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u65b0\u9879\u76ee\u5f00\u5c55\u5b9e\u65bd", type=LogType.DELETE)
    @RequestMapping(value={"/{id}/new-project-items"}, method={RequestMethod.DELETE})
    public void deleteNewProjectItem(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().deleteNewProjectItem(id, wrapper);
    }
}
