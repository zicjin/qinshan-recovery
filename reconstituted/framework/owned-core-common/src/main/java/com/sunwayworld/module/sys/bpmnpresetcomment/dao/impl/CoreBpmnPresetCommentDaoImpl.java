/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmnpresetcomment.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.bpmnpresetcomment.bean.CoreBpmnPresetCommentBean;
import com.sunwayworld.module.sys.bpmnpresetcomment.dao.CoreBpmnPresetCommentDao;
import com.sunwayworld.module.sys.bpmnpresetcomment.mapper.CoreBpmnPresetCommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreBpmnPresetCommentDaoImpl
extends MybatisDaoSupport<CoreBpmnPresetCommentBean, Long>
implements CoreBpmnPresetCommentDao {
    @Autowired
    private CoreBpmnPresetCommentMapper coreBpmnPresetCommentMapper;

    public CoreBpmnPresetCommentMapper getMapper() {
        return this.coreBpmnPresetCommentMapper;
    }
}
