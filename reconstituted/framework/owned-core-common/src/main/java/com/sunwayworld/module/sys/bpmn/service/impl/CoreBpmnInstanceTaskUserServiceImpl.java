/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.bpmn.dao.CoreBpmnInstanceTaskUserDao;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnInstanceTaskUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreBpmnInstanceTaskUserServiceImpl
implements CoreBpmnInstanceTaskUserService {
    @Autowired
    private CoreBpmnInstanceTaskUserDao instanceTaskUserDao;

    @Override
    public CoreBpmnInstanceTaskUserDao getDao() {
        return this.instanceTaskUserDao;
    }
}
