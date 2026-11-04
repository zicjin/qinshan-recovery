/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.annualplan.service.impl;

import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreAnnualPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreDailySupervisionPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreEmpTrainPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreEquFuncCheckPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreInternalAuditPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreManageReviewPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.bean.CorePeriodVerifyPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreQualityControlPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreVerifyCalibratePlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.persistent.dao.CoreAnnualPlanDao;
import com.sunwayworld.cloud.module.qms.annualplan.service.CoreAnnualPlanService;
import com.sunwayworld.cloud.module.qms.annualplan.service.CoreDailySupervisionPlanService;
import com.sunwayworld.cloud.module.qms.annualplan.service.CoreEmpTrainPlanService;
import com.sunwayworld.cloud.module.qms.annualplan.service.CoreEquFuncCheckPlanService;
import com.sunwayworld.cloud.module.qms.annualplan.service.CoreInternalAuditPlanService;
import com.sunwayworld.cloud.module.qms.annualplan.service.CoreManageReviewPlanService;
import com.sunwayworld.cloud.module.qms.annualplan.service.CorePeriodVerifyPlanService;
import com.sunwayworld.cloud.module.qms.annualplan.service.CoreQualityControlPlanService;
import com.sunwayworld.cloud.module.qms.annualplan.service.CoreVerifyCalibratePlanService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessStatus;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreAnnualPlanServiceImpl
implements CoreAnnualPlanService {
    @Autowired
    private CoreAnnualPlanDao coreAnnualPlanDao;
    @Autowired
    @Lazy
    private CoreEmpTrainPlanService coreEmpTrainPlanService;
    @Autowired
    @Lazy
    private CoreInternalAuditPlanService coreInternalAuditPlanService;
    @Autowired
    @Lazy
    private CoreManageReviewPlanService coreManageReviewPlanService;
    @Autowired
    @Lazy
    private CoreDailySupervisionPlanService coreDailySupervisionPlanService;
    @Autowired
    @Lazy
    private CoreQualityControlPlanService coreQualityControlPlanService;
    @Autowired
    @Lazy
    private CoreVerifyCalibratePlanService coreVerifyCalibratePlanService;
    @Autowired
    @Lazy
    private CorePeriodVerifyPlanService corePeriodVerifyPlanService;
    @Autowired
    @Lazy
    private CoreEquFuncCheckPlanService coreEquFuncCheckPlanService;
    @Autowired
    @Lazy
    private CoreFileService coreFileService;

    @Override
    public CoreAnnualPlanDao getDao() {
        return this.coreAnnualPlanDao;
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.INSERT")
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreAnnualPlanBean coreAnnualPlan = jsonWrapper.parseUnique(CoreAnnualPlanBean.class);
        coreAnnualPlan.setId(ApplicationContextHelper.getNextIdentity());
        coreAnnualPlan.setAnnualPlanNo(ApplicationContextHelper.getNextSequence(this.getDao().getTable(), "NDJH"));
        this.getDao().insert(coreAnnualPlan);
        if ("mReviewPlan".equalsIgnoreCase(coreAnnualPlan.getPlanCategory())) {
            CoreManageReviewPlanBean coreManageReviewPlan = new CoreManageReviewPlanBean();
            coreManageReviewPlan.setId(ApplicationContextHelper.getNextIdentity());
            coreManageReviewPlan.setAnnualPlanId(coreAnnualPlan.getId());
            this.coreManageReviewPlanService.getDao().insert((CoreManageReviewPlanBean)coreManageReviewPlan);
        } else if ("inAuditPlan".equalsIgnoreCase(coreAnnualPlan.getPlanCategory())) {
            CoreInternalAuditPlanBean coreInternalAuditPlan = new CoreInternalAuditPlanBean();
            coreInternalAuditPlan.setId(ApplicationContextHelper.getNextIdentity());
            coreInternalAuditPlan.setAnnualPlanId(coreAnnualPlan.getId());
            this.coreInternalAuditPlanService.getDao().insert((CoreInternalAuditPlanBean)coreInternalAuditPlan);
        } else if ("dailySupervise".equalsIgnoreCase(coreAnnualPlan.getPlanCategory())) {
            CoreDailySupervisionPlanBean coreDailySupervisionPlan = new CoreDailySupervisionPlanBean();
            coreDailySupervisionPlan.setId(ApplicationContextHelper.getNextIdentity());
            coreDailySupervisionPlan.setAnnualPlanId(coreAnnualPlan.getId());
            this.coreDailySupervisionPlanService.getDao().insert((CoreDailySupervisionPlanBean)coreDailySupervisionPlan);
        } else if ("qualityControl".equalsIgnoreCase(coreAnnualPlan.getPlanCategory())) {
            CoreQualityControlPlanBean CoreQualityControlPlan = new CoreQualityControlPlanBean();
            CoreQualityControlPlan.setId(ApplicationContextHelper.getNextIdentity());
            CoreQualityControlPlan.setAnnualPlanId(coreAnnualPlan.getId());
            this.coreQualityControlPlanService.getDao().insert((CoreQualityControlPlanBean)CoreQualityControlPlan);
        }
        return coreAnnualPlan.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long copyAnnualPlan(Long id, RestJsonWrapperBean wrapper) {
        CoreAnnualPlanBean sourceCoreAnnualPlanBean = (CoreAnnualPlanBean)this.getDao().selectById(id);
        CoreAnnualPlanBean targetCoreAnnualPlanBean = new CoreAnnualPlanBean();
        targetCoreAnnualPlanBean.setId(ApplicationContextHelper.getNextIdentity());
        targetCoreAnnualPlanBean.setProcessStatus(ProcessStatus.DRAFT.name().toLowerCase());
        targetCoreAnnualPlanBean.setAnnualPlanNo(ApplicationContextHelper.getNextSequence(this.getDao().getTable(), "NDJH"));
        targetCoreAnnualPlanBean.setPlanCategory(sourceCoreAnnualPlanBean.getPlanCategory());
        targetCoreAnnualPlanBean.setPlanYear(sourceCoreAnnualPlanBean.getPlanYear());
        targetCoreAnnualPlanBean.setEstimatedDate(sourceCoreAnnualPlanBean.getEstimatedDate());
        targetCoreAnnualPlanBean.setRemark(sourceCoreAnnualPlanBean.getRemark());
        this.getDao().insert(targetCoreAnnualPlanBean);
        switch (targetCoreAnnualPlanBean.getPlanCategory()) {
            case "empTrainPlan": {
                List<CoreEmpTrainPlanBean> coreEmpTrainPlanList = this.coreEmpTrainPlanService.selectListByFilter(SearchFilter.instance().match("ANNUALPLANID", id).filter(MatchPattern.EQ), wrapper);
                coreEmpTrainPlanList.forEach(e -> {
                    e.setId(ApplicationContextHelper.getNextIdentity());
                    e.setAnnualPlanId(targetCoreAnnualPlanBean.getId());
                });
                this.coreEmpTrainPlanService.getDao().insert(coreEmpTrainPlanList);
                break;
            }
            case "mReviewPlan": {
                List<CoreManageReviewPlanBean> coreManageReviewPlanList = this.coreManageReviewPlanService.selectListByFilter(SearchFilter.instance().match("ANNUALPLANID", id).filter(MatchPattern.EQ), wrapper);
                coreManageReviewPlanList.forEach(e -> {
                    e.setId(ApplicationContextHelper.getNextIdentity());
                    e.setAnnualPlanId(targetCoreAnnualPlanBean.getId());
                });
                this.coreManageReviewPlanService.getDao().insert(coreManageReviewPlanList);
                break;
            }
            case "inAuditPlan": {
                List<CoreInternalAuditPlanBean> coreInternalAuditPlanList = this.coreInternalAuditPlanService.selectListByFilter(SearchFilter.instance().match("ANNUALPLANID", id).filter(MatchPattern.EQ), wrapper);
                coreInternalAuditPlanList.forEach(e -> {
                    e.setId(ApplicationContextHelper.getNextIdentity());
                    e.setAnnualPlanId(targetCoreAnnualPlanBean.getId());
                });
                this.coreInternalAuditPlanService.getDao().insert(coreInternalAuditPlanList);
                break;
            }
            case "dailySupervise": {
                List<CoreDailySupervisionPlanBean> coreDailySupervisionPlanList = this.coreDailySupervisionPlanService.selectListByFilter(SearchFilter.instance().match("ANNUALPLANID", id).filter(MatchPattern.EQ), wrapper);
                coreDailySupervisionPlanList.forEach(e -> {
                    e.setId(ApplicationContextHelper.getNextIdentity());
                    e.setAnnualPlanId(targetCoreAnnualPlanBean.getId());
                });
                this.coreDailySupervisionPlanService.getDao().insert(coreDailySupervisionPlanList);
                break;
            }
            case "qualityControl": {
                List<CoreQualityControlPlanBean> coreQualityControlPlanList = this.coreQualityControlPlanService.selectListByFilter(SearchFilter.instance().match("ANNUALPLANID", id).filter(MatchPattern.EQ), wrapper);
                coreQualityControlPlanList.forEach(e -> {
                    e.setId(ApplicationContextHelper.getNextIdentity());
                    e.setAnnualPlanId(targetCoreAnnualPlanBean.getId());
                });
                this.coreQualityControlPlanService.getDao().insert(coreQualityControlPlanList);
                break;
            }
            case "verifyCalibrate": {
                List<CoreVerifyCalibratePlanBean> coreVerifyCalibratePlanList = this.coreVerifyCalibratePlanService.selectListByFilter(SearchFilter.instance().match("ANNUALPLANID", id).filter(MatchPattern.EQ), wrapper);
                coreVerifyCalibratePlanList.forEach(e -> {
                    e.setId(ApplicationContextHelper.getNextIdentity());
                    e.setAnnualPlanId(targetCoreAnnualPlanBean.getId());
                });
                this.coreVerifyCalibratePlanService.getDao().insert(coreVerifyCalibratePlanList);
                break;
            }
            case "periodVerify": {
                List<CorePeriodVerifyPlanBean> corePeriodVerifyPlanList = this.corePeriodVerifyPlanService.selectListByFilter(SearchFilter.instance().match("ANNUALPLANID", id).filter(MatchPattern.EQ), wrapper);
                corePeriodVerifyPlanList.forEach(e -> {
                    e.setId(ApplicationContextHelper.getNextIdentity());
                    e.setAnnualPlanId(targetCoreAnnualPlanBean.getId());
                });
                this.corePeriodVerifyPlanService.getDao().insert(corePeriodVerifyPlanList);
                break;
            }
            case "equFuncCheck": {
                List<CoreEquFuncCheckPlanBean> coreEquFuncCheckPlanList = this.coreEquFuncCheckPlanService.selectListByFilter(SearchFilter.instance().match("ANNUALPLANID", id).filter(MatchPattern.EQ), wrapper);
                coreEquFuncCheckPlanList.forEach(e -> {
                    e.setId(ApplicationContextHelper.getNextIdentity());
                    e.setAnnualPlanId(targetCoreAnnualPlanBean.getId());
                });
                this.coreEquFuncCheckPlanService.getDao().insert(coreEquFuncCheckPlanList);
                break;
            }
        }
        this.coreFileService.copy(this.getDao().getTable() + "$" + sourceCoreAnnualPlanBean.getId(), this.getDao().getTable() + "$" + targetCoreAnnualPlanBean.getId());
        return targetCoreAnnualPlanBean.getId();
    }
}
