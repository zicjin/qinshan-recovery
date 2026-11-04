/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.office.resource.impl;

import com.sunwayworld.cloud.module.office.bean.CoreOfficeToolbarBean;
import com.sunwayworld.cloud.module.office.bean.CoreOfficeToolbarDTO;
import com.sunwayworld.cloud.module.office.bean.CoreOfficeToolbarLineBean;
import com.sunwayworld.cloud.module.office.resource.CoreOfficeToolbarResource;
import com.sunwayworld.cloud.module.office.service.CoreOfficeToolbarService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.suspendable.resource.GenericSuspendableResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="Office\u5de5\u5177\u680f\u7ba1\u7406")
@RestController
@GikamBean
@RequestMapping(value={"/secure/core/module/office/toolbars"})
public class CoreOfficeToolbarResourceImpl
implements CoreOfficeToolbarResource,
AbstractGenericResource<CoreOfficeToolbarService, CoreOfficeToolbarBean, Long>,
GenericSuspendableResource<CoreOfficeToolbarService, CoreOfficeToolbarBean, Long> {
    @Autowired
    private CoreOfficeToolbarService coreOfficeToolbarService;

    @Override
    public CoreOfficeToolbarService getService() {
        return this.coreOfficeToolbarService;
    }

    @Override
    @Log(value="\u65b0\u589e\u5de5\u5177\u680f", type=LogType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @RequestMapping(value={"/{toolbarId}/lines"}, method={RequestMethod.POST})
    @Log(value="\u6dfb\u52a0\u5de5\u5177\u680f\u660e\u7ec6", type=LogType.INSERT)
    public Long insertLine(@PathVariable(value="toolbarId") Long toolbarId, RestJsonWrapperBean wrapper) {
        return this.getService().insertLine(toolbarId, wrapper);
    }

    @Override
    @RequestMapping(value={"/{toolbarId}/lines/queries"}, method={RequestMethod.POST})
    @Log(value="\u67e5\u8be2\u5de5\u5177\u680f\u660e\u7ec6", type=LogType.SELECT)
    public Page<CoreOfficeToolbarLineBean> selectOfficeToolbarLinePagation(@PathVariable(value="toolbarId") Long toolbarId, RestJsonWrapperBean wrapper) {
        return this.getService().selectLinePagination(toolbarId, wrapper);
    }

    @Override
    @RequestMapping(value={"/lines"}, method={RequestMethod.DELETE})
    @Log(value="\u5220\u9664\u5de5\u5177\u680f\u660e\u7ec6", type=LogType.DELETE)
    public void deleteLine(RestJsonWrapperBean wrapper) {
        this.getService().deleteLine(wrapper);
    }

    @Override
    @RequestMapping(value={"/queries/category/{officeCategory}"}, method={RequestMethod.POST})
    @Log(value="\u67e5\u8be2\u5de5\u5177\u680f", type=LogType.SELECT)
    public Page<CoreOfficeToolbarDTO> selectOfficeToolbarPagetion(@PathVariable(value="officeCategory") String officeCategory, RestJsonWrapperBean wrapper) {
        return this.getService().selectToolbarPagination(officeCategory, wrapper);
    }

    @Override
    @RequestMapping(value={"/lines/action/suspend"}, method={RequestMethod.PUT})
    @Log(value="\u4e8c\u7ea7\u83dc\u5355\u680f\u6ce8\u9500", type=LogType.SUSPEND)
    public void suspend(RestJsonWrapperBean wrapper) {
        this.getService().suspendLine(wrapper);
    }

    @Override
    @Log(value="\u542f\u7528\uff08\u53d6\u6d88\u6ce8\u9500\uff09\u4e8c\u7ea7\u83dc\u5355\u680f", type=LogType.SUSPEND)
    @RequestMapping(value={"/lines/action/unsuspend"}, method={RequestMethod.PUT})
    public void unsuspend(RestJsonWrapperBean wrapper) {
        this.getService().unsuspendLine(wrapper);
    }
}
