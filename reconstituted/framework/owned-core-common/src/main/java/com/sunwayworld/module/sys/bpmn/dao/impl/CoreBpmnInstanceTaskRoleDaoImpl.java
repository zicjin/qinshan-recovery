/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskRoleBean;
import com.sunwayworld.module.sys.bpmn.dao.CoreBpmnInstanceTaskRoleDao;
import com.sunwayworld.module.sys.bpmn.mapper.CoreBpmnInstanceTaskRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreBpmnInstanceTaskRoleDaoImpl
extends MybatisDaoSupport<CoreBpmnInstanceTaskRoleBean, Long>
implements CoreBpmnInstanceTaskRoleDao {
    @Autowired
    private CoreBpmnInstanceTaskRoleMapper instanceTaskRoleMapper;

    public CoreBpmnInstanceTaskRoleMapper getMapper() {
        return this.instanceTaskRoleMapper;
    }
}
