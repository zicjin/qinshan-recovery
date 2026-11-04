/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.annualplan.resource.impl;

import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreAnnualPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreDailySupervisionPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreEmpTrainPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreEquFuncCheckPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreInternalAuditPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreManageReviewPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.bean.CorePeriodVerifyPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreQualityControlPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreVerifyCalibratePlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.resource.CoreAnnualPlanResource;
import com.sunwayworld.cloud.module.qms.annualplan.service.CoreAnnualPlanService;
import com.sunwayworld.cloud.module.qms.annualplan.service.CoreDailySupervisionPlanService;
import com.sunwayworld.cloud.module.qms.annualplan.service.CoreEmpTrainPlanService;
import com.sunwayworld.cloud.module.qms.annualplan.service.CoreEquFuncCheckPlanService;
import com.sunwayworld.cloud.module.qms.annualplan.service.CoreInternalAuditPlanService;
import com.sunwayworld.cloud.module.qms.annualplan.service.CoreManageReviewPlanService;
import com.sunwayworld.cloud.module.qms.annualplan.service.CorePeriodVerifyPlanService;
import com.sunwayworld.cloud.module.qms.annualplan.service.CoreQualityControlPlanService;
import com.sunwayworld.cloud.module.qms.annualplan.service.CoreVerifyCalibratePlanService;
import com.sunwayworld.cloud.module.qms.annualplan.validator.CoreAnnualPlanStartValidator;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.choosable.resource.AbstractGenericChoosableResource;
import com.sunwayworld.framework.support.selectable.resource.AbstractGenericSelectableResource;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u5e74\u5ea6\u8ba1\u5212")
@RestController
@GikamBean
public class CoreAnnualPlanResourceImpl
implements CoreAnnualPlanResource,
GenericAuditableResource<CoreAnnualPlanService, CoreAnnualPlanBean, Long>,
AbstractGenericResource<CoreAnnualPlanService, CoreAnnualPlanBean, Long>,
AbstractGenericChoosableResource<CoreAnnualPlanService, CoreAnnualPlanBean, Long>,
AbstractGenericSelectableResource<CoreAnnualPlanService, CoreAnnualPlanBean, Long> {
    @Autowired
    private CoreAnnualPlanService coreAnnualPlanService;
    @Autowired
    @Lazy
    private CoreDailySupervisionPlanService coreDailySupervisionPlanService;
    @Autowired
    @Lazy
    private CoreEmpTrainPlanService coreEmpTrainPlanService;
    @Autowired
    @Lazy
    private CoreEquFuncCheckPlanService coreEquFuncCheckPlanService;
    @Autowired
    private CoreInternalAuditPlanService coreInternalAuditPlanService;
    @Autowired
    private CoreManageReviewPlanService coreManageReviewPlanService;
    @Autowired
    private CorePeriodVerifyPlanService corePeriodVerifyPlanService;
    @Autowired
    private CoreVerifyCalibratePlanService coreVerifyCalibratePlanService;
    @Autowired
    private CoreQualityControlPlanService coreQualityControlPlanService;

    @Override
    public CoreAnnualPlanService getService() {
        return this.coreAnnualPlanService;
    }

    @Override
    @Log(value="\u65b0\u589e\u5e74\u5ea6\u8ba1\u5212", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u542f\u52a8", type=LogType.PROCESS)
    @RequestMapping(value={"/action/start-process"}, method={RequestMethod.PUT})
    @ValidateDataWith(value=CoreAnnualPlanStartValidator.class)
    public void startProcess(RestJsonWrapperBean wrapper) {
        this.getService().startProcess(wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u5458\u5de5\u57f9\u8bad\u8ba1\u5212", type=LogType.SELECT)
    @RequestMapping(value={"/emp-train-plan/queries"}, method={RequestMethod.POST})
    public Page<CoreEmpTrainPlanBean> selectEmpTrainPlanPagination(RestJsonWrapperBean wrapper) {
        return this.coreEmpTrainPlanService.selectPagination(wrapper);
    }

    @Override
    @Log(value="\u4fdd\u5b58\u5458\u5de5\u57f9\u8bad\u8ba1\u5212", type=LogType.UPDATE)
    @RequestMapping(value={"/{annualPlanId}/action/save-emp-train-plans"}, method={RequestMethod.PUT})
    public void saveEmpTrainPlan(@PathVariable Long annualPlanId, RestJsonWrapperBean wrapper) {
        this.coreEmpTrainPlanService.saveEmpTrainPlan(annualPlanId, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u5458\u5de5\u57f9\u8bad\u8ba1\u5212", type=LogType.DELETE)
    @RequestMapping(value={"/action/delete-emp-train-plans"}, method={RequestMethod.DELETE})
    public void deleteEmpTrainPlan(RestJsonWrapperBean wrapper) {
        this.coreEmpTrainPlanService.delete(wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u4eea\u5668\u8bbe\u5907\u529f\u80fd\u68c0\u67e5\u8ba1\u5212", type=LogType.SELECT)
    @RequestMapping(value={"/equ-func-check-plan/queries"}, method={RequestMethod.POST})
    public Page<CoreEquFuncCheckPlanBean> selectEquFuncCheckPlanPagination(RestJsonWrapperBean wrapper) {
        return this.coreEquFuncCheckPlanService.selectPagination(wrapper);
    }

    @Override
    @Log(value="\u4fdd\u5b58\u4eea\u5668\u8bbe\u5907\u529f\u80fd\u68c0\u67e5\u8ba1\u5212", type=LogType.UPDATE)
    @RequestMapping(value={"/{annualPlanId}/action/save-equ-func-check-plans"}, method={RequestMethod.PUT})
    public void saveEquFuncCheckPlan(@PathVariable Long annualPlanId, RestJsonWrapperBean wrapper) {
        this.coreEquFuncCheckPlanService.saveEquFuncCheckPlan(annualPlanId, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u4eea\u5668\u8bbe\u5907\u529f\u80fd\u68c0\u67e5\u8ba1\u5212", type=LogType.DELETE)
    @RequestMapping(value={"/action/delete-equ-func-check-plans"}, method={RequestMethod.DELETE})
    public void deleteEquFuncCheckPlan(RestJsonWrapperBean wrapper) {
        this.coreEquFuncCheckPlanService.delete(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u5e74\u5ea6\u8ba1\u5212id\u67e5\u8be2\u5185\u90e8\u5ba1\u6838\u8ba1\u5212\u8be6\u60c5", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/internal-audit-plan"}, method={RequestMethod.GET})
    public CoreInternalAuditPlanBean selectInternalAuditPlanPagination(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return (CoreInternalAuditPlanBean)this.coreInternalAuditPlanService.selectFirstByFilter(SearchFilter.instance().match("ANNUALPLANID", id).filter(MatchPattern.EQ), new Order[0]);
    }

    @Override
    @Log(value="\u6839\u636e\u5e74\u5ea6\u8ba1\u5212id\u67e5\u8be2\u7ba1\u7406\u8bc4\u5ba1\u8ba1\u5212\u8be6\u60c5\u9875", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/manage-review-plan"}, method={RequestMethod.GET})
    public CoreManageReviewPlanBean selectManageReviewPlanPagination(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return (CoreManageReviewPlanBean)this.coreManageReviewPlanService.selectFirstByFilter(SearchFilter.instance().match("ANNUALPLANID", id).filter(MatchPattern.EQ), new Order[0]);
    }

    @Override
    @Log(value="\u67e5\u8be2\u671f\u95f4\u6838\u67e5\u8ba1\u5212", type=LogType.SELECT)
    @RequestMapping(value={"/period-verify-plan/queries"}, method={RequestMethod.POST})
    public Page<CorePeriodVerifyPlanBean> selectPeriodVerifyPlanPagination(RestJsonWrapperBean wrapper) {
        return this.corePeriodVerifyPlanService.selectPagination(wrapper);
    }

    @Override
    @Log(value="\u4fdd\u5b58\u671f\u95f4\u6838\u67e5\u8ba1\u5212", type=LogType.UPDATE)
    @RequestMapping(value={"/{annualPlanId}/action/save-period-verify-plans"}, method={RequestMethod.PUT})
    public void savePeriodVerifyPlan(@PathVariable Long annualPlanId, RestJsonWrapperBean wrapper) {
        this.corePeriodVerifyPlanService.savePeriodVerifyPlan(annualPlanId, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u671f\u95f4\u6838\u67e5\u8ba1\u5212", type=LogType.DELETE)
    @RequestMapping(value={"/action/delete-period-verify-plans"}, method={RequestMethod.DELETE})
    public void deletePeriodVerifyPlan(RestJsonWrapperBean wrapper) {
        this.corePeriodVerifyPlanService.delete(wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u4eea\u5668\u8bbe\u5907\u68c0\u5b9a\u6821\u51c6\u8ba1\u5212", type=LogType.SELECT)
    @RequestMapping(value={"/verify-calibrate-plan/queries"}, method={RequestMethod.POST})
    public Page<CoreVerifyCalibratePlanBean> selectVerifyCalibratePlanPagination(RestJsonWrapperBean wrapper) {
        return this.coreVerifyCalibratePlanService.selectPagination(wrapper);
    }

    @Override
    @Log(value="\u4fdd\u5b58\u4eea\u5668\u8bbe\u5907\u68c0\u5b9a\u6821\u51c6\u8ba1\u5212", type=LogType.UPDATE)
    @RequestMapping(value={"/{annualPlanId}/action/save-verify-calibrate-plans"}, method={RequestMethod.PUT})
    public void saveVerifyCalibratePlan(@PathVariable Long annualPlanId, RestJsonWrapperBean wrapper) {
        this.coreVerifyCalibratePlanService.saveVerifyCalibratePlan(annualPlanId, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u4eea\u5668\u8bbe\u5907\u68c0\u5b9a\u6821\u51c6\u8ba1\u5212", type=LogType.DELETE)
    @RequestMapping(value={"/action/delete-verify-calibrate-plans"}, method={RequestMethod.DELETE})
    public void deleteVerifyCalibratePlan(RestJsonWrapperBean wrapper) {
        this.coreVerifyCalibratePlanService.delete(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u5e74\u5ea6\u8ba1\u5212id\u67e5\u8be2\u68c0\u9a8c\u8d28\u91cf\u63a7\u5236\u8ba1\u5212\u8be6\u60c5\u9875", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/quality-control-plan"}, method={RequestMethod.GET})
    public CoreQualityControlPlanBean selectQualityControlPlanPagination(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return (CoreQualityControlPlanBean)this.coreQualityControlPlanService.selectFirstByFilter(SearchFilter.instance().match("ANNUALPLANID", id).filter(MatchPattern.EQ), new Order[0]);
    }

    @Override
    @Log(value="\u6839\u636e\u5e74\u5ea6\u8ba1\u5212id\u67e5\u8be2\u65e5\u5e38\u76d1\u7763\u8ba1\u5212\u8be6\u60c5\u9875", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/daily-supervision-plan"}, method={RequestMethod.GET})
    public CoreDailySupervisionPlanBean selectDailySupervisionPlanPagination(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return (CoreDailySupervisionPlanBean)this.coreDailySupervisionPlanService.selectFirstByFilter(SearchFilter.instance().match("ANNUALPLANID", id).filter(MatchPattern.EQ), new Order[0]);
    }

    @Override
    @Log(value="\u590d\u5236\u5e74\u5ea6\u8ba1\u5212", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/action/copy"}, method={RequestMethod.POST})
    public Long copyAnnualPlan(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().copyAnnualPlan(id, wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u8bbe\u5907\u529f\u80fd\u68c0\u67e5\u8ba1\u5212\u9009\u62e9\u9875\u6570\u636e", type=LogType.SELECT)
    @RequestMapping(value={"/equ-func-check-plan/queries/choosable"}, method={RequestMethod.POST})
    public Page<CoreEquFuncCheckPlanBean> selectEquFuncCheckPlanChoosablePagination(RestJsonWrapperBean wrapper) {
        return this.coreEquFuncCheckPlanService.selectChoosablePagination(wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u4eea\u5668\u8bbe\u5907\u68c0\u5b9a\u6821\u51c6\u8ba1\u5212\u9009\u62e9\u9875\u6570\u636e", type=LogType.SELECT)
    @RequestMapping(value={"/verify-calibrate-plan/queries/choosable"}, method={RequestMethod.POST})
    public Page<CoreVerifyCalibratePlanBean> selectVerifyCalibratePlanChoosablePagination(RestJsonWrapperBean wrapper) {
        return this.coreVerifyCalibratePlanService.selectChoosablePagination(wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u671f\u95f4\u6838\u67e5\u8ba1\u5212\u9009\u62e9\u9875\u6570\u636e", type=LogType.SELECT)
    @RequestMapping(value={"/period-verify-plan/queries/choosable"}, method={RequestMethod.POST})
    public Page<CorePeriodVerifyPlanBean> selectPeriodVerifyPlanChoosablePagination(RestJsonWrapperBean wrapper) {
        return this.corePeriodVerifyPlanService.selectChoosablePagination(wrapper);
    }
}
