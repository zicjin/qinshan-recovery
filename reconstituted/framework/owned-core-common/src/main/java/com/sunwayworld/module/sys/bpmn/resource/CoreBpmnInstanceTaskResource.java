/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskRoleBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskUserBean;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnInstanceTaskService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/sys/bpmn-inst-tasks"})
public interface CoreBpmnInstanceTaskResource
extends GenericResource<CoreBpmnInstanceTaskService, CoreBpmnInstanceTaskBean, Long> {
    @Log(value="\u67e5\u8be2\u6d41\u7a0b\u6267\u884c\u5b9e\u4f8b\u7684\u4efb\u52a1\u4e0b\u7684\u5ba1\u6838\u4eba\u5458", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/bpmn-inst-task-users/queries"}, method={RequestMethod.POST})
    public Page<CoreBpmnInstanceTaskUserBean> selectInstTaskUserList(@PathVariable Long var1, RestJsonWrapperBean var2);

    @Log(value="\u67e5\u8be2\u6d41\u7a0b\u6267\u884c\u5b9e\u4f8b\u7684\u4efb\u52a1\u4e0b\u7684\u5ba1\u6838\u89d2\u8272", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/bpmn-inst-task-roles/queries"}, method={RequestMethod.POST})
    public Page<CoreBpmnInstanceTaskRoleBean> selectInstTaskRoleList(@PathVariable Long var1, RestJsonWrapperBean var2);

    @Log(value="\u65b0\u589e\u6d41\u7a0b\u6267\u884c\u5b9e\u4f8b\u7684\u4efb\u52a1\u4e0b\u7684\u5ba1\u6838\u4eba\u5458", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/bpmn-inst-task-users"}, method={RequestMethod.POST})
    public void insertInstTaskUser(@PathVariable Long var1, RestJsonWrapperBean var2);

    @Log(value="\u65b0\u589e\u6d41\u7a0b\u6267\u884c\u5b9e\u4f8b\u7684\u4efb\u52a1\u4e0b\u7684\u5ba1\u6838\u89d2\u8272", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/bpmn-inst-task-roles"}, method={RequestMethod.POST})
    public void insertInstTaskRole(@PathVariable Long var1, RestJsonWrapperBean var2);

    @Log(value="\u5220\u9664\u6d41\u7a0b\u6267\u884c\u5b9e\u4f8b\u7684\u4efb\u52a1\u4e0b\u7684\u5ba1\u6838\u4eba\u5458", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/bpmn-inst-task-users"}, method={RequestMethod.DELETE})
    public void deleteInstTaskUser(@PathVariable Long var1, RestJsonWrapperBean var2);

    @Log(value="\u5220\u9664\u6d41\u7a0b\u6267\u884c\u5b9e\u4f8b\u7684\u4efb\u52a1\u4e0b\u7684\u5ba1\u6838\u89d2\u8272", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/bpmn-inst-task-roles"}, method={RequestMethod.DELETE})
    public void deleteInstTaskRole(@PathVariable Long var1, RestJsonWrapperBean var2);
}
