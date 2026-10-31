package com.sunwayworld.emc.module.bp.result.run.validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;
import com.sunwayworld.emc.module.bp.result.run.service.EmcRunService;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

@GikamBean
@Repository
public class EmcRunAuditSubmitValidator implements DataValidator {

    @Autowired
    private EmcOrderService orderService;

    @Autowired
    private EmcOrderTaskService orderTaskService;

    @Autowired
    private EmcRunService runService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        List<EmcProjectBean> projectList = wrapper.parse(EmcProjectBean.class);

        List<EmcOrderTaskBean> filterOrderTaskList = new ArrayList<>();
        projectList.forEach(project -> {
            EmcOrderTaskBean filterOrderTask = new EmcOrderTaskBean();
            filterOrderTask.setProjId(project.getId());
            filterOrderTask.setMonitorTmplId(project.getExt$Item("monitortmplid"));
            filterOrderTask.setAnalysisType("analysis");
            filterOrderTaskList.add(filterOrderTask);
        });
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectList(filterOrderTaskList, Arrays.asList("PROJID", "MONITORTMPLID", "ANALYSISTYPE"), Arrays.asList("ID", "ORDERID", "STATUS", "RUNID"));

        // 查询已终止的样品，对于已经终止的样品不再校验
        List<Long> orderIdList = orderTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
        List<EmcOrderBean> orderList = orderService.getDao().selectListByIds(orderIdList, Arrays.asList("ID", "TERMINATION"));
        List<Long> terminatedOrderIdList = orderList.stream().filter(order -> StringUtils.equals(Constant.YES, order.getTermination())).map(EmcOrderBean::getId).collect(Collectors.toList());

        List<EmcOrderTaskBean> validOrderTaskList = orderTaskList.stream().filter(task -> !terminatedOrderIdList.contains(task.getOrderId())).collect(Collectors.toList());

        // 过滤出还未添加批的样品测试，排除已经终止的   //已分包的数据runId也为空但是status == done
        List<EmcOrderTaskBean> noRunOrderTaskList = validOrderTaskList.stream().filter(task -> ObjectUtils.isEmpty(task.getRunId()) && !"done".equals(task.getStatus())).collect(Collectors.toList());
        if (!noRunOrderTaskList.isEmpty()) {
            this.addConstraintViolation("EMC.MODULE.BP.RESULT.RUNS.TIP.EXIST_UNFINISHED_ORDER_TASK");
            return false;
        }

        // 过滤出已经添加了批的样品测试
        List<String> runIdList = validOrderTaskList.stream().filter(task -> !ObjectUtils.isEmpty(task.getRunId())).map(EmcOrderTaskBean::getRunId).collect(Collectors.toList());
        List<EmcRunBean> runList = runService.getDao().selectListByOneColumnValues(runIdList, "ID", Arrays.asList("ID", "CHECKSTATUS"));
        List<String> checkStatusList = runList.stream().map(EmcRunBean::getCheckStatus).collect(Collectors.toList());
        boolean unchecked = checkStatusList.stream().anyMatch(checkStatus -> !StringUtils.equals(checkStatus, Constant.YES));
        if (unchecked) {
            this.addConstraintViolation("EMC.MODULE.BP.RESULT.RUNS.TIP.EXIST_UNFINISHED_RUN");
            return false;
        }

        return true;
    }

}
