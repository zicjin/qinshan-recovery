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
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnProcOrgBean;
import com.sunwayworld.module.sys.bpmn.resource.CoreBpmnProcResource;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnProcService;
import com.sunwayworld.module.sys.bpmn.validator.CoreBpmnProcDeletableValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@LogModule(value="\u5de5\u4f5c\u6d41\u7a0b")
@GikamBean
public class CoreBpmnProcResourceImpl
implements CoreBpmnProcResource {
    @Autowired
    private CoreBpmnProcService procService;

    @Override
    public CoreBpmnProcService getService() {
        return this.procService;
    }

    @Override
    public String getBpmnInputTextUrl(@PathVariable Long id) {
        return this.getService().getBpmnInputTextUrl(id);
    }

    @Override
    public String getBpmnInputTextUrl(RestJsonWrapperBean wrapper) {
        return this.getService().getBpmnInputTextUrl(wrapper);
    }

    @Override
    @Log(value="\u901a\u8fc7\u6d41\u7a0b\u5b9a\u4e49\u7f16\u7801\u67e5\u8be2\u4f7f\u7528\u90e8\u95e8", type=LogType.SELECT)
    public Page<CoreBpmnProcOrgBean> selectProcOrgPagination(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectProcOrgPagination(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664", type=LogType.DELETE)
    @RequestMapping(method={RequestMethod.DELETE})
    @ValidateDataWith(value=CoreBpmnProcDeletableValidator.class)
    public void delete(RestJsonWrapperBean wrapper) {
        CoreBpmnProcResource.super.delete(wrapper);
    }
}
