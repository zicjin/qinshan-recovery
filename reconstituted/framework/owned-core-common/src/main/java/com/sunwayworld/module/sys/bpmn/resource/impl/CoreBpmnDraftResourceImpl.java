/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.resource.impl;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnDraftOrgBean;
import com.sunwayworld.module.sys.bpmn.resource.CoreBpmnDraftResource;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnDraftService;
import com.sunwayworld.module.sys.bpmn.validator.CoreBpmnDraftSubmittableValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@LogModule(value="\u5de5\u4f5c\u6d41\u8349\u7a3f\u529f\u80fd")
@GikamBean
public class CoreBpmnDraftResourceImpl
implements CoreBpmnDraftResource {
    @Autowired
    private CoreBpmnDraftService draftService;

    @Override
    public CoreBpmnDraftService getService() {
        return this.draftService;
    }

    @Override
    @Log(value="\u65b0\u589e", type=LogType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
        return this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u63d0\u4ea4", type=LogType.UPDATE)
    @ValidateDataWith(value=CoreBpmnDraftSubmittableValidator.class)
    public void submit(@PathVariable Long id) {
        this.getService().submit(id);
    }

    @Override
    @Log(value="\u53d8\u66f4", type=LogType.INSERT)
    public Long insertByProcId(@PathVariable Long procId) {
        return this.getService().insertByProcId(procId);
    }

    @Override
    @Log(value="\u901a\u8fc7\u6d41\u7a0b\u8349\u7a3f\u7f16\u7801\u67e5\u8be2\u4f7f\u7528\u90e8\u95e8", type=LogType.SELECT)
    public Page<CoreBpmnDraftOrgBean> selectDraftOrgPagination(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectDraftOrgPagination(id, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u4f7f\u7528\u90e8\u95e8", type=LogType.INSERT)
    public void insertDraftOrg(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().insertDraftOrg(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u4f7f\u7528\u90e8\u95e8", type=LogType.DELETE)
    public void deleteDraftOrg(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().deleteDraftOrg(id, wrapper);
    }
}
