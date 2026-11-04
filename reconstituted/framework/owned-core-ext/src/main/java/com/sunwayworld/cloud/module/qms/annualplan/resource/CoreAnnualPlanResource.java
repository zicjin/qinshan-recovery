/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.annualplan.resource;

import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreAnnualPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreDailySupervisionPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreEmpTrainPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreEquFuncCheckPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreInternalAuditPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreManageReviewPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.bean.CorePeriodVerifyPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreQualityControlPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreVerifyCalibratePlanBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableCloudResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/qms/annual-plans"})
public interface CoreAnnualPlanResource
extends GenericCloudResource<CoreAnnualPlanBean, Long>,
GenericChoosableCloudResource<CoreAnnualPlanBean, Long>,
GenericSelectableCloudResource<CoreAnnualPlanBean, Long> {
    public Page<CoreEmpTrainPlanBean> selectEmpTrainPlanPagination(RestJsonWrapperBean var1);

    public void saveEmpTrainPlan(Long var1, RestJsonWrapperBean var2);

    public Page<CoreEquFuncCheckPlanBean> selectEquFuncCheckPlanPagination(RestJsonWrapperBean var1);

    public void saveEquFuncCheckPlan(Long var1, RestJsonWrapperBean var2);

    public CoreInternalAuditPlanBean selectInternalAuditPlanPagination(Long var1, RestJsonWrapperBean var2);

    public CoreManageReviewPlanBean selectManageReviewPlanPagination(Long var1, RestJsonWrapperBean var2);

    public Page<CorePeriodVerifyPlanBean> selectPeriodVerifyPlanPagination(RestJsonWrapperBean var1);

    public void savePeriodVerifyPlan(Long var1, RestJsonWrapperBean var2);

    public Page<CoreVerifyCalibratePlanBean> selectVerifyCalibratePlanPagination(RestJsonWrapperBean var1);

    public void saveVerifyCalibratePlan(Long var1, RestJsonWrapperBean var2);

    public CoreQualityControlPlanBean selectQualityControlPlanPagination(Long var1, RestJsonWrapperBean var2);

    public CoreDailySupervisionPlanBean selectDailySupervisionPlanPagination(Long var1, RestJsonWrapperBean var2);

    public void deleteEmpTrainPlan(RestJsonWrapperBean var1);

    public void deleteEquFuncCheckPlan(RestJsonWrapperBean var1);

    public void deletePeriodVerifyPlan(RestJsonWrapperBean var1);

    public void deleteVerifyCalibratePlan(RestJsonWrapperBean var1);

    public Long copyAnnualPlan(Long var1, RestJsonWrapperBean var2);

    public Page<CoreEquFuncCheckPlanBean> selectEquFuncCheckPlanChoosablePagination(RestJsonWrapperBean var1);

    public Page<CoreVerifyCalibratePlanBean> selectVerifyCalibratePlanChoosablePagination(RestJsonWrapperBean var1);

    public Page<CorePeriodVerifyPlanBean> selectPeriodVerifyPlanChoosablePagination(RestJsonWrapperBean var1);
}
