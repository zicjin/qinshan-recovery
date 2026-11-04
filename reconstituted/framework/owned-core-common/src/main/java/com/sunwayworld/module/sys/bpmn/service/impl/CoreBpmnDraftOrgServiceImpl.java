/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.bpmn.dao.CoreBpmnDraftOrgDao;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnDraftOrgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreBpmnDraftOrgServiceImpl
implements CoreBpmnDraftOrgService {
    @Autowired
    private CoreBpmnDraftOrgDao draftOrgDao;

    @Override
    public CoreBpmnDraftOrgDao getDao() {
        return this.draftOrgDao;
    }
}
