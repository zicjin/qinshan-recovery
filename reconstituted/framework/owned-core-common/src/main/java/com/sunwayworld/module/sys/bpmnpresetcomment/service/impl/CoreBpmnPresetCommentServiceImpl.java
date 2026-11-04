/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmnpresetcomment.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.bpmnpresetcomment.bean.CoreBpmnPresetCommentBean;
import com.sunwayworld.module.sys.bpmnpresetcomment.dao.CoreBpmnPresetCommentDao;
import com.sunwayworld.module.sys.bpmnpresetcomment.service.CoreBpmnPresetCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreBpmnPresetCommentServiceImpl
implements CoreBpmnPresetCommentService {
    @Autowired
    private CoreBpmnPresetCommentDao coreBpmnPresetCommentDao;

    @Override
    public CoreBpmnPresetCommentDao getDao() {
        return this.coreBpmnPresetCommentDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreBpmnPresetCommentBean presetComment = jsonWrapper.parseUnique(CoreBpmnPresetCommentBean.class);
        presetComment.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(presetComment);
        return presetComment.getId();
    }
}
