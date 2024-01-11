package com.sunwayworld.emc.module.bp.project.projscheme.validator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

/**
 * @author zhengxy@sunwayworld.com 2022年05月18日
 */
@Repository
public class PfDeleteOrderValidator implements DataValidator {

    @Autowired
    private EmcOrderTaskService orderTaskService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class);

        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderIdList, "orderId", Arrays.asList("id", "status", "runId"));

        // 如果样品已流转至报告环节，则不让删除
        /*boolean hasDone = orderTaskList.stream().anyMatch(task -> EmcBizConstant.ORDERTASK_STATUS_DONE.equals(task.getStatus()));
        if (hasDone) {
            this.addConstraintViolation("EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TIP.CANNOT_DELETE_ORDER_FROM_REPORT");
            return false;
        }*/

        List<String> runIdList = orderTaskList.stream().filter(task -> !StringUtils.isEmpty(task.getRunId())).map(EmcOrderTaskBean::getRunId).distinct().collect(Collectors.toList());
        if (!runIdList.isEmpty()) {
            this.addConstraintViolation("EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TIP.CANNOT_DELETE_ORDER_WITH_RUN");
            return false;
        }

        return true;
    }
}
