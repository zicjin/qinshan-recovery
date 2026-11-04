package com.sunwayworld.cloud.module.qms.annualplan.validator;

import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreAnnualPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreDailySupervisionPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreEmpTrainPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreEquFuncCheckPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreInternalAuditPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreManageReviewPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.bean.CorePeriodVerifyPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreQualityControlPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreVerifyCalibratePlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.service.CoreAnnualPlanService;
import com.sunwayworld.cloud.module.qms.annualplan.service.CoreDailySupervisionPlanService;
import com.sunwayworld.cloud.module.qms.annualplan.service.CoreEmpTrainPlanService;
import com.sunwayworld.cloud.module.qms.annualplan.service.CoreEquFuncCheckPlanService;
import com.sunwayworld.cloud.module.qms.annualplan.service.CoreInternalAuditPlanService;
import com.sunwayworld.cloud.module.qms.annualplan.service.CoreManageReviewPlanService;
import com.sunwayworld.cloud.module.qms.annualplan.service.CorePeriodVerifyPlanService;
import com.sunwayworld.cloud.module.qms.annualplan.service.CoreQualityControlPlanService;
import com.sunwayworld.cloud.module.qms.annualplan.service.CoreVerifyCalibratePlanService;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessStatus;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class CoreAnnualPlanStartValidator implements DataValidator {
   @Autowired
   private CoreAnnualPlanService coreAnnualPlanService;
   @Autowired
   @Lazy
   private CoreEmpTrainPlanService coreEmpTrainPlanService;
   @Autowired
   @Lazy
   private CoreManageReviewPlanService coreManageReviewPlanService;
   @Autowired
   @Lazy
   private CoreDailySupervisionPlanService coreDailySupervisionPlanService;
   @Autowired
   @Lazy
   private CoreEquFuncCheckPlanService coreEquFuncCheckPlanService;
   @Autowired
   @Lazy
   private CoreInternalAuditPlanService coreInternalAuditPlanService;
   @Autowired
   @Lazy
   private CorePeriodVerifyPlanService corePeriodVerifyPlanService;
   @Autowired
   @Lazy
   private CoreVerifyCalibratePlanService coreVerifyCalibratePlanService;
   @Autowired
   @Lazy
   private CoreQualityControlPlanService coreQualityControlPlanService;

   @Override
   public boolean doValid(Object... args) {
      RestJsonWrapperBean wrapper = (RestJsonWrapperBean)args[0];
      List<Long> annualPlanIdList = wrapper.parseId(Long.class);
      List<CoreAnnualPlanBean> coreAnnualPlanList = this.coreAnnualPlanService.selectListByIds(annualPlanIdList);
      boolean existChild = true;
      boolean requiredValid = true;

      for (CoreAnnualPlanBean coreAnnualPlan : coreAnnualPlanList) {
         String planCategory = coreAnnualPlan.getPlanCategory();
         String orgId = coreAnnualPlan.getCreatedByOrgId();
         String planYear = coreAnnualPlan.getPlanYear();
         if (StringUtils.isEmpty(planYear) || coreAnnualPlan.getEstimatedDate() == null) {
            this.addConstraintViolation(I18nHelper.getMessage("QMS.MODULE.DOVALID.TIP.EXIST_REQUIRED_COLUMN_IS_EMPTY_MODIFY_BEFORE_SUBMIT"));
            return false;
         }

         SearchFilter searchFilterFindChild = SearchFilter.instance().match("ANNUALPLANID", coreAnnualPlan.getId()).filter(MatchPattern.EQ);
         switch (planCategory) {
            case "empTrainPlan":
               List<CoreEmpTrainPlanBean> coreEmpTrainPlanList = this.coreEmpTrainPlanService.selectListByFilter(searchFilterFindChild, new Order[0]);
               existChild = !coreEmpTrainPlanList.isEmpty();
               break;
            case "mReviewPlan":
               List<CoreManageReviewPlanBean> coreManageReviewPlanList = this.coreManageReviewPlanService
                  .selectListByFilter(searchFilterFindChild, new Order[0]);
               existChild = !coreManageReviewPlanList.isEmpty();
               if (existChild) {
                  requiredValid = coreManageReviewPlanList.stream()
                     .noneMatch(
                        e -> StringUtils.isEmpty(e.getReviewPurpose())
                           || StringUtils.isEmpty(e.getReviewContent())
                           || StringUtils.isEmpty(e.getReviewReq())
                           || StringUtils.isEmpty(e.getReviewer())
                           || StringUtils.isEmpty(e.getReviewSchedule())
                           || StringUtils.isEmpty(e.getReviewReport())
                           || StringUtils.isEmpty(e.getReviewFrequency())
                           || StringUtils.isEmpty(e.getRemark())
                     );
               }
               break;
            case "dailySupervise":
               List<CoreDailySupervisionPlanBean> coreDailySupervisionPlanList = this.coreDailySupervisionPlanService
                  .selectListByFilter(searchFilterFindChild, new Order[0]);
               existChild = !coreDailySupervisionPlanList.isEmpty();
               if (existChild) {
                  requiredValid = coreDailySupervisionPlanList.stream()
                     .noneMatch(
                        e -> StringUtils.isEmpty(e.getSupervisePurpose())
                           || StringUtils.isEmpty(e.getSuperviseContent())
                           || StringUtils.isEmpty(e.getSuperviseReq())
                           || StringUtils.isEmpty(e.getSuperviseType())
                           || StringUtils.isEmpty(e.getSupervisorName())
                           || StringUtils.isEmpty(e.getRemark())
                     );
               }
               break;
            case "equFuncCheck":
               List<CoreEquFuncCheckPlanBean> coreEquFuncCheckPlanList = this.coreEquFuncCheckPlanService
                  .selectListByFilter(searchFilterFindChild, new Order[0]);
               existChild = !coreEquFuncCheckPlanList.isEmpty();
               break;
            case "inAuditPlan":
               List<CoreInternalAuditPlanBean> coreInternalAuditPlanList = this.coreInternalAuditPlanService
                  .selectListByFilter(searchFilterFindChild, new Order[0]);
               existChild = !coreInternalAuditPlanList.isEmpty();
               if (existChild) {
                  requiredValid = coreInternalAuditPlanList.stream()
                     .noneMatch(
                        e -> StringUtils.isEmpty(e.getAuditPurpose())
                           || StringUtils.isEmpty(e.getAuditContent())
                           || StringUtils.isEmpty(e.getAuditRule())
                           || StringUtils.isEmpty(e.getAuditMethod())
                           || e.getAuditDate() == null
                           || StringUtils.isEmpty(e.getRemark())
                     );
               }
               break;
            case "periodVerify":
               List<CorePeriodVerifyPlanBean> corePeriodVerifyPlanList = this.corePeriodVerifyPlanService
                  .selectListByFilter(searchFilterFindChild, new Order[0]);
               existChild = !corePeriodVerifyPlanList.isEmpty();
               break;
            case "qualityControl":
               List<CoreQualityControlPlanBean> coreQualityControlPlanList = this.coreQualityControlPlanService
                  .selectListByFilter(searchFilterFindChild, new Order[0]);
               existChild = !coreQualityControlPlanList.isEmpty();
               if (existChild) {
                  requiredValid = coreQualityControlPlanList.stream()
                     .noneMatch(
                        e -> StringUtils.isEmpty(e.getItemName())
                           || StringUtils.isEmpty(e.getQcType())
                           || StringUtils.isEmpty(e.getStandard())
                           || StringUtils.isEmpty(e.getAcceptCriteria())
                           || e.getCarryDate() == null
                           || StringUtils.isEmpty(e.getRemark())
                     );
               }
               break;
            case "verifyCalibrate":
               List<CoreVerifyCalibratePlanBean> coreVerifyCalibratePlanList = this.coreVerifyCalibratePlanService
                  .selectListByFilter(searchFilterFindChild, new Order[0]);
               existChild = !coreVerifyCalibratePlanList.isEmpty();
         }

         if (!existChild) {
            this.addConstraintViolation(I18nHelper.getMessage("QMS.MODULE.ANNUALPLAN.CONTAIN_ONE_CHILD"));
            return false;
         }

         if (!requiredValid) {
            this.addConstraintViolation(I18nHelper.getMessage("QMS.MODULE.DOVALID.TIP.EXIST_REQUIRED_COLUMN_IS_EMPTY_MODIFY_BEFORE_SUBMIT"));
            return false;
         }

         SearchFilter searchFilterFindSubmit = SearchFilter.instance()
            .match("PLANCATEGORY", planCategory)
            .filter(MatchPattern.EQ)
            .match("CREATEDBYORGID", orgId)
            .filter(MatchPattern.EQ)
            .match("ID", coreAnnualPlan.getId())
            .filter(MatchPattern.DIFFER)
            .match("PROCESSSTATUS", ProcessStatus.APPROVE_OR_DONE.name().toLowerCase())
            .filter(MatchPattern.SPECIAL)
            .match("PLANYEAR", planYear)
            .filter(MatchPattern.EQ);
         List<CoreAnnualPlanBean> startedCoreAnnualPlanList = this.coreAnnualPlanService.selectListByFilter(searchFilterFindSubmit, new Order[0]);
         if (!startedCoreAnnualPlanList.isEmpty()) {
            this.addConstraintViolation(I18nHelper.getMessage("QMS.MODULE.ANNUALPLAN.CAN_PUT_ONCE_ORG_CATEGORY"));
            return false;
         }
      }

      return true;
   }
}
