package com.sunwayworld.emc.module.bp.bp.validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

/**
 * @author zhengxy@sunwayworld.com 2022年10月19日
 */
@Repository
public class PfSubpackageTestValidator implements DataValidator {

    @Autowired
    private EmcOrderTaskService orderTaskService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class);
        List<EmcOrderItemGroupBean> itemGroupList = wrapper.parse(EmcOrderItemGroupBean.class);

        List<EmcOrderItemGroupBean> unSyncGroupList = itemGroupList.stream().filter(group -> StringUtils.equals(Constant.YES, group.getExt$Item("syncStatus"))).collect(Collectors.toList());
        if (!unSyncGroupList.isEmpty()) {
            this.addConstraintViolation("请先同步新增测试后，再进行分包设置");
            return false;
        }

        List<EmcOrderTaskBean> filterTaskList = new ArrayList<>();

        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        orderIdList.forEach(orderId -> itemGroupList.stream().filter(group -> ObjectUtils.equals(orderId, group.getOrderId())).forEach(group -> {
            EmcOrderTaskBean filterTask = new EmcOrderTaskBean();
            filterTask.setOrderId(orderId);
            filterTask.setTestMethodId(group.getTestMethodId());
            filterTaskList.add(filterTask);
        }));

        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectList(filterTaskList, Arrays.asList("orderId", "testMethodId"), Arrays.asList("id", "runId"));

        boolean hasCreatedRun = orderTaskList.stream().anyMatch(task -> !StringUtils.isEmpty(task.getRunId()));
        if (hasCreatedRun) {
            this.addConstraintViolation("所选的测试已创建了分析批，不允许分包");
            return false;
        }

        return true;
    }
}
