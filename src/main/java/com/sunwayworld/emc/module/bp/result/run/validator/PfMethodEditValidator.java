package com.sunwayworld.emc.module.bp.result.run.validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

/**
 * @author zhengxy@sunwayworld.com 2022年04月13日
 */
@Repository
public class PfMethodEditValidator implements DataValidator {

    @Autowired
    private EmcOrderTaskService orderTaskService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class, 0);

        List<Long> orderIdList = orderTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
        Long testMethodId = orderTaskList.get(0).getTestMethodId();
        List<EmcOrderTaskBean> filterTaskList = new ArrayList<>(orderIdList.size());
        orderIdList.forEach(orderId -> {
            EmcOrderTaskBean filterTask = new EmcOrderTaskBean();
            filterTask.setOrderId(orderId);
            filterTask.setTestMethodId(testMethodId);
            filterTaskList.add(filterTask);
        });
        List<EmcOrderTaskBean> allTaskList = orderTaskService.getDao().selectList(filterTaskList, Arrays.asList("testMethodId", "orderId"), Arrays.asList("id", "analysisType", "orderCategory"));

        // 如果方法下的分析项既有采样，也有分析类型的，除了气之外，其他样品类型不允许修改方法
        boolean notGas = allTaskList.stream().anyMatch(task -> !StringUtils.contains(task.getOrderCategory(), PfConstant.ORDERCATEGORY_GAS));
        boolean notMatched = allTaskList.stream().anyMatch(task -> !StringUtils.equals(EmcBizConstant.ANALYSISCATEGORY_ANALYSIS, task.getAnalysisType()));

        if (notMatched && notGas) {
            this.addConstraintViolation("EMC.MODULE.BP.RESULT.RUNS.TIP.CAN_NOT_EDIT_METHOD");
            return false;
        }

        return true;
    }
}
