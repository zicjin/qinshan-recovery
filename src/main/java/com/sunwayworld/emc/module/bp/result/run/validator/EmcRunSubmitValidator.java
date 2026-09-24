package com.sunwayworld.emc.module.bp.result.run.validator;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.result.run.service.EmcRunService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

@GikamBean
@Repository
public class EmcRunSubmitValidator implements DataValidator {

    @Autowired
    private EmcOrderTaskService orderTaskService;

    @Autowired
    private EmcOrderService orderService;

    @Autowired
    private EmcRunService emcRunService;

    @Autowired
    private EmcOrderTaskResultService orderTaskResultService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        List<EmcRunBean> runList = wrapper.parse(EmcRunBean.class);
        List<String> runIdList = runList.stream().map(EmcRunBean::getId).collect(Collectors.toList());
        runList = emcRunService.selectListByIds(runIdList);

        // 筛选状态为logged记录
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(runIdList, "RUNID").stream().filter(e -> "logged".equals(e.getStatus()))
                .collect(Collectors.toList());
        if (orderTaskList.isEmpty()) {
            this.addConstraintViolation("EMC.MODULE.BP.RESULT.RUNS.TIP.EMPTY_SAMPLE");
            return false;
        }
        List<Long> orderIdList = orderTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
        List<EmcOrderBean> orderList = orderService.selectListByIds(orderIdList);
        for (EmcRunBean run : runList) {
            List<Long> curOrderIdList = orderTaskList.stream().filter(e -> ObjectUtils.equals(run.getId(), e.getRunId())).map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
            LocalDate minSampleBeginDate = orderList.stream().filter(e -> curOrderIdList.contains(e.getId())).map(EmcOrderBean::getSampleBeginDate).min(Comparator.nullsLast(LocalDate::compareTo)).orElse(null);
            if (minSampleBeginDate != null && DateTimeUtils.isBefore(run.getAnalysisBeginDate(), minSampleBeginDate)) {
                this.addConstraintViolation("分析日期小于采样日期，不允许提交");
                return false;
            }
        }

        List<Long> orderTaskIdList = orderTaskList.stream().map(e -> e.getId()).distinct().collect(Collectors.toList());
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "ORDERTASKID");
        if (orderTaskResultList.isEmpty()) {
            this.addConstraintViolation("EMC.MODULE.BP.RESULT.RUNS.TIP.EMPTY_RESULT");
            return false;
        }

        if (orderTaskResultList.stream().anyMatch(e -> StringUtils.isEmpty(e.getReportResult()))) {

            orderTaskResultList = orderTaskResultList.stream().filter(e -> StringUtils.isEmpty(e.getReportResult())).collect(Collectors.toList());
            List<String> ordernoList = orderTaskResultList.stream().map(e -> e.getOrderNo()).distinct().collect(Collectors.toList());
            String ordernos = "";
            for(String orderno : ordernoList){
                ordernos = ordernos + orderno +",";
            }
            if(ordernos.length()>1){
                ordernos = ordernos.substring(0,ordernos.length()-1);
            }

            this.addConstraintViolation("样品"+ordernos+"的数据没有录入完整，不能提交");
            return false;
        }



        return true;
    }

}
