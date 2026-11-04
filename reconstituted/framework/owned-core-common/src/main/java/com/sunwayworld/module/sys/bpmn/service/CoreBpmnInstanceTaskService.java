/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.service;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskRoleBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskUserBean;
import java.util.List;
import org.springframework.lang.Nullable;

public interface CoreBpmnInstanceTaskService
extends GenericService<CoreBpmnInstanceTaskBean, Long> {
    public List<CoreBpmnInstanceTaskBean> selectAuditableInstanceTaskList(@Nullable String var1, List<Long> var2);

    public void cleanCandidatorAsync(Long var1);

    public Page<CoreBpmnInstanceTaskUserBean> selectInstTaskUserList(Long var1, RestJsonWrapperBean var2);

    public Page<CoreBpmnInstanceTaskRoleBean> selectInstTaskRoleList(Long var1, RestJsonWrapperBean var2);

    public void insertInstTaskUser(Long var1, RestJsonWrapperBean var2);

    public void deleteInstTaskUser(Long var1, RestJsonWrapperBean var2);

    public void insertInstTaskRole(Long var1, RestJsonWrapperBean var2);

    public void deleteInstTaskRole(Long var1, RestJsonWrapperBean var2);
}
