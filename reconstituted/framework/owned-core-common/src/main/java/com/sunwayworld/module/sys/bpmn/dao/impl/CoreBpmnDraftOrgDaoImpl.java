/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnDraftOrgBean;
import com.sunwayworld.module.sys.bpmn.dao.CoreBpmnDraftOrgDao;
import com.sunwayworld.module.sys.bpmn.mapper.CoreBpmnDraftOrgMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreBpmnDraftOrgDaoImpl
extends MybatisDaoSupport<CoreBpmnDraftOrgBean, Long>
implements CoreBpmnDraftOrgDao {
    @Autowired
    private CoreBpmnDraftOrgMapper draftOrgMapper;

    public CoreBpmnDraftOrgMapper getMapper() {
        return this.draftOrgMapper;
    }
}
