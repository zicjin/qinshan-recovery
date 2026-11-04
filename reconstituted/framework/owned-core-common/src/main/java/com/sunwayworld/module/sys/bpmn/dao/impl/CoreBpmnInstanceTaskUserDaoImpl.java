/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskUserBean;
import com.sunwayworld.module.sys.bpmn.dao.CoreBpmnInstanceTaskUserDao;
import com.sunwayworld.module.sys.bpmn.mapper.CoreBpmnInstanceTaskUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreBpmnInstanceTaskUserDaoImpl
extends MybatisDaoSupport<CoreBpmnInstanceTaskUserBean, Long>
implements CoreBpmnInstanceTaskUserDao {
    @Autowired
    private CoreBpmnInstanceTaskUserMapper instanceTaskUserMapper;

    public CoreBpmnInstanceTaskUserMapper getMapper() {
        return this.instanceTaskUserMapper;
    }
}
