/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnTargetBean;
import com.sunwayworld.module.sys.bpmn.dao.CoreBpmnTargetDao;
import com.sunwayworld.module.sys.bpmn.mapper.CoreBpmnTargetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreBpmnTargetDaoImpl
extends MybatisDaoSupport<CoreBpmnTargetBean, String>
implements CoreBpmnTargetDao {
    @Autowired
    private CoreBpmnTargetMapper targetMapper;

    public CoreBpmnTargetMapper getMapper() {
        return this.targetMapper;
    }
}
