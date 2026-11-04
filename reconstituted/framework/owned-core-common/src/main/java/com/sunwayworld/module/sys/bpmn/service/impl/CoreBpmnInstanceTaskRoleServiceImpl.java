/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.bpmn.dao.CoreBpmnInstanceTaskRoleDao;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnInstanceTaskRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreBpmnInstanceTaskRoleServiceImpl
implements CoreBpmnInstanceTaskRoleService {
    @Autowired
    private CoreBpmnInstanceTaskRoleDao instanceTaskRoleDao;

    @Override
    public CoreBpmnInstanceTaskRoleDao getDao() {
        return this.instanceTaskRoleDao;
    }
}
