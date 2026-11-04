/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.exam.service.impl;

import com.sunwayworld.cloud.module.train.exam.bean.CoreExamBean;
import com.sunwayworld.cloud.module.train.exam.persistent.dao.CoreExamDao;
import com.sunwayworld.cloud.module.train.exam.service.CoreExamService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.exception.InvalidDataException;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreExamServiceImpl
implements CoreExamService {
    @Autowired
    private CoreExamDao coreExamDao;

    @Override
    public CoreExamDao getDao() {
        return this.coreExamDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean wrapper) {
        CoreExamBean exam = wrapper.parseUnique(CoreExamBean.class);
        exam.setId(ApplicationContextHelper.getNextIdentity());
        exam.setLastUpdatedById(LocalContextHelper.getLoginUserId());
        exam.setLastUpdatedByName(LocalContextHelper.getLoginUserName());
        exam.setLastUpdatedTime(LocalDateTime.now());
        this.getDao().insert(exam);
        return exam.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.UPDATE)
    @Audit(value="AUDIT.SAVE")
    public void updateIfChanged(List<CoreExamBean> itemList) {
        CoreUserBean currentUser = LocalContextHelper.getLoginUser();
        LocalDateTime currentTime = LocalDateTime.now();
        itemList.forEach(e -> {
            e.setLastUpdatedById(currentUser.getId());
            e.setLastUpdatedByName(currentUser.getUserName());
            e.setLastUpdatedTime(currentTime);
        });
        CoreExamService.super.updateIfChanged(itemList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.ACTIVATE)
    @Audit(value="AUDIT.ACTIVATE")
    public void activate(RestJsonWrapperBean wrapper) {
        List<CoreExamBean> itemList = wrapper.parse(this.getDao().getType());
        if (!this.doValid(itemList)) {
            throw new InvalidDataException("CORE.MODULE.TRAIN.EXAM.TIP.LOCKED_FAILED");
        }
        itemList.forEach(i -> this.setActivateProperties(i, true));
        itemList.forEach(e -> {
            e.setLastUpdatedById(LocalContextHelper.getLoginUserId());
            e.setLastUpdatedByName(LocalContextHelper.getLoginUser().getUserName());
            e.setLastUpdatedTime(LocalDateTime.now());
        });
        this.getDao().update(itemList, new String[]{"ACTIVATEDFLAG", "ACTIVATEDBYID", "ACTIVATEDBYNAME", "ACTIVATEDTIME", "LASTUPDATEDBYID", "LASTUPDATEDBYNAME", "LASTUPDATEDTIME"});
    }

    private boolean doValid(List<CoreExamBean> itemList) {
        boolean validFlag = true;
        for (CoreExamBean exam : itemList) {
            if (!"manual".equals(exam.getQuestionMethod()) || exam.getTmplSingleChoiceQty().intValue() == exam.getSingleChoiceQty().intValue() && exam.getTmplMultipleChoiceQty().intValue() == exam.getMultipleChoiceQty().intValue() && exam.getTmplJudgementQty().intValue() == exam.getJudgementQty().intValue() && exam.getTmplShortAnswerQty().intValue() == exam.getShortAnswerQty().intValue() && exam.getTmplCompletionQty().intValue() == exam.getCompletionQty().intValue()) continue;
            validFlag = false;
            break;
        }
        return validFlag;
    }
}
