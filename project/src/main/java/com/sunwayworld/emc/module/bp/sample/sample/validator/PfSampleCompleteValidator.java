package com.sunwayworld.emc.module.bp.sample.sample.validator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

/**
 * @author zhengxy@sunwayworld.com 2023年09月06日
 */
@Repository
public class PfSampleCompleteValidator implements DataValidator {

    @Autowired
    @Lazy
    private EmcOrderService orderService;

    @Autowired
    @Lazy
    private EmcOrderTaskService orderTaskService;

    @Autowired
    @Lazy
    private EmcOrderTaskResultService orderTaskResultService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        List<EmcSampleBean> sampleList = wrapper.parse(EmcSampleBean.class);
        List<Long> sampleIdList = sampleList.stream().map(EmcSampleBean::getId).collect(Collectors.toList());

        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(sampleIdList, "sampleId", Arrays.asList("id", "sampleId"));
        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderIdList, "orderId", Arrays.asList("id", "analysisType"));

        List<Long> samplingTaskIdList = orderTaskList.stream().filter(task -> EmcBizConstant.ANALYSISCATEGORY_SAMPLING.equals(task.getAnalysisType())).map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        if (!samplingTaskIdList.isEmpty()) {
            List<EmcOrderTaskResultBean> resultList = orderTaskResultService.getDao().selectListByOneColumnValues(samplingTaskIdList, "orderTaskId", Arrays.asList("id", "reportResult"));
            boolean uncompleted = resultList.stream().anyMatch(result -> StringUtils.isEmpty(result.getReportResult()));
            if (uncompleted) {
                this.addConstraintViolation("EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.CANNOT_SUBMIT_WITH_NO_RESULT");
                return false;
            }
        }

        return true;
    }
}
