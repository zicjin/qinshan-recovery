/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.bpmn.dao.CoreBpmnProcOrgDao;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnProcOrgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreBpmnProcOrgServiceImpl
implements CoreBpmnProcOrgService {
    @Autowired
    private CoreBpmnProcOrgDao procOrgDao;

    @Override
    public CoreBpmnProcOrgDao getDao() {
        return this.procOrgDao;
    }
}
