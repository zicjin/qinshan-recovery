/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.resource.impl;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskRoleBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskUserBean;
import com.sunwayworld.module.sys.bpmn.resource.CoreBpmnInstanceTaskResource;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnInstanceTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@LogModule(value="\u6d41\u7a0b\u6267\u884c\u5b9e\u4f8b")
@GikamBean
public class CoreBpmnInstanceTaskResourceImpl
implements CoreBpmnInstanceTaskResource {
    @Autowired
    private CoreBpmnInstanceTaskService instanceTaskService;

    @Override
    public CoreBpmnInstanceTaskService getService() {
        return this.instanceTaskService;
    }

    @Override
    public Page<CoreBpmnInstanceTaskUserBean> selectInstTaskUserList(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectInstTaskUserList(id, wrapper);
    }

    @Override
    public Page<CoreBpmnInstanceTaskRoleBean> selectInstTaskRoleList(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectInstTaskRoleList(id, wrapper);
    }

    @Override
    public void insertInstTaskUser(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().insertInstTaskUser(id, wrapper);
    }

    @Override
    public void insertInstTaskRole(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().insertInstTaskRole(id, wrapper);
    }

    @Override
    public void deleteInstTaskUser(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().deleteInstTaskUser(id, wrapper);
    }

    @Override
    public void deleteInstTaskRole(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().deleteInstTaskRole(id, wrapper);
    }
}
