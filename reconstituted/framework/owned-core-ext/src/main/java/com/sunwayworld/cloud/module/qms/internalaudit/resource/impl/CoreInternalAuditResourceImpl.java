/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.internalaudit.resource.impl;

import com.sunwayworld.cloud.module.qms.internalaudit.bean.CoreInternalAuditBean;
import com.sunwayworld.cloud.module.qms.internalaudit.bean.CoreInternalAuditGroupBean;
import com.sunwayworld.cloud.module.qms.internalaudit.bean.CoreInternalAuditNcrBean;
import com.sunwayworld.cloud.module.qms.internalaudit.resource.CoreInternalAuditResource;
import com.sunwayworld.cloud.module.qms.internalaudit.service.CoreInternalAuditService;
import com.sunwayworld.cloud.module.qms.internalaudit.validator.CoreInternalAuditCompleteTaskValidator;
import com.sunwayworld.cloud.module.qms.internalaudit.validator.CoreInternalAuditSubmitTaskValidator;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u5185\u90e8\u5ba1\u6838")
@RestController
@GikamBean
public class CoreInternalAuditResourceImpl
implements CoreInternalAuditResource,
GenericAuditableResource<CoreInternalAuditService, CoreInternalAuditBean, Long>,
AbstractGenericResource<CoreInternalAuditService, CoreInternalAuditBean, Long>,
GenericChoosableResource<CoreInternalAuditService, CoreInternalAuditBean, Long> {
    @Autowired
    private CoreInternalAuditService coreInternalAuditService;

    @Override
    public CoreInternalAuditService getService() {
        return this.coreInternalAuditService;
    }

    @Override
    @Log(value="\u65b0\u589e\u5185\u90e8\u5ba1\u6838", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u5185\u90e8\u5ba1\u6838ID\u67e5\u8be2\u5185\u90e8\u5ba1\u6838\u7ec4", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/audit-groups/queries"}, method={RequestMethod.POST})
    public Page<CoreInternalAuditGroupBean> selectAuditGroupPaginationByInternalAuditId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectAuditGroupPaginationByInternalAuditId(id, wrapper);
    }

    @Override
    @Log(value="\u4fdd\u5b58\u5185\u90e8\u5ba1\u6838\u7ec4", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/audit-groups"}, method={RequestMethod.PUT})
    public void saveAuditGroup(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().saveAuditGroup(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u5185\u90e8\u5ba1\u6838\u7ec4", type=LogType.DELETE)
    @RequestMapping(value={"/audit-groups"}, method={RequestMethod.DELETE})
    public void deleteAuditGroup(RestJsonWrapperBean wrapper) {
        this.getService().deleteAuditGroup(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u5185\u90e8\u5ba1\u6838ID\u67e5\u8be2\u4e0d\u7b26\u5408\u6c47\u603b", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/audit-ncrs/queries"}, method={RequestMethod.POST})
    public Page<CoreInternalAuditNcrBean> selectAuditNcrPaginationByInternalAuditId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectAuditNcrPaginationByInternalAuditId(id, wrapper);
    }

    @Override
    @Log(value="\u4fdd\u5b58\u4e0d\u7b26\u5408\u6c47\u603b", type=LogType.UPDATE)
    @RequestMapping(value={"/{id}/audit-ncrs"}, method={RequestMethod.PUT})
    public void saveAuditNcr(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().saveAuditNcr(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u4e0d\u7b26\u5408\u6c47\u603b", type=LogType.DELETE)
    @RequestMapping(value={"/audit-ncrs"}, method={RequestMethod.DELETE})
    public void deleteAuditNcr(RestJsonWrapperBean wrapper) {
        this.getService().deleteAuditNcr(wrapper);
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u542f\u52a8", type=LogType.PROCESS)
    @RequestMapping(value={"/action/start-process"}, method={RequestMethod.PUT})
    @ValidateDataWith(value=CoreInternalAuditSubmitTaskValidator.class)
    public void startProcess(RestJsonWrapperBean wrapper) {
        this.getService().startProcess(wrapper);
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u5ba1\u6838\u901a\u8fc7", type=LogType.PROCESS)
    @RequestMapping(value={"/action/complete-task"}, method={RequestMethod.PUT})
    @ValidateDataWith(value=CoreInternalAuditCompleteTaskValidator.class)
    public void completeTask(RestJsonWrapperBean wrapper) {
        this.getService().completeTask(wrapper);
    }
}
