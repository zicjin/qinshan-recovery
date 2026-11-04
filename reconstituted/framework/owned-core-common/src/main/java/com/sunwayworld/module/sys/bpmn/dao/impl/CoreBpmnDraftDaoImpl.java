/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnDraftBean;
import com.sunwayworld.module.sys.bpmn.dao.CoreBpmnDraftDao;
import com.sunwayworld.module.sys.bpmn.mapper.CoreBpmnDraftMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreBpmnDraftDaoImpl
extends MybatisDaoSupport<CoreBpmnDraftBean, Long>
implements CoreBpmnDraftDao {
    @Autowired
    private CoreBpmnDraftMapper draftMapper;

    public CoreBpmnDraftMapper getMapper() {
        return this.draftMapper;
    }
}
