package com.sunwayworld.emc.module.bp.bp.validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

/**
 * @author zhengxy@sunwayworld.com 2022年05月25日
 */
@Repository
public class PfDeleteOrderItemGroupValidator implements DataValidator {

    @Autowired
    private EmcOrderTaskService orderTaskService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        List<EmcOrderItemGroupBean> orderItemGroupList = wrapper.parse(EmcOrderItemGroupBean.class);

        List<EmcOrderTaskBean> filterTaskList = new ArrayList<>(orderItemGroupList.size());
        orderItemGroupList.forEach(group -> {
            EmcOrderTaskBean filterTask = new EmcOrderTaskBean();
            filterTask.setOrderId(group.getOrderId());
            filterTask.setTestMethodId(group.getTestMethodId());
            filterTaskList.add(filterTask);
        });

        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectList(filterTaskList, Arrays.asList("orderId", "testMethodId"), Arrays.asList("id", "runId"));

        boolean hasDone = orderTaskList.stream().anyMatch(task -> EmcBizConstant.ORDERTASK_STATUS_DONE.equals(task.getStatus()));
        if (hasDone) {
            this.addConstraintViolation("EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TIP.CANNOT_DELETE_TEST_FROM_DONE");
            return false;
        }

        List<String> runIdList = orderTaskList.stream().filter(task -> !StringUtils.isEmpty(task.getRunId())).map(EmcOrderTaskBean::getRunId).distinct().collect(Collectors.toList());
        if (!runIdList.isEmpty()) {
            this.addConstraintViolation("EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TIP.CANNOT_DELETE_TEST_WITH_RUN");
            return false;
        }

        return true;
    }
}
