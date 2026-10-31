package com.sunwayworld.emc.module.bp.result.run.validator;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

@GikamBean
@Repository
public class PfRunAuditSubmitValidator extends EmcRunAuditSubmitValidator implements DataValidator {

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
        
        List<Long> orderTaskIdList = noRunOrderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskOrgList = orderTaskService.getDao().selectListByOneColumnValues(orderTaskIdList, "ID");
        //区别还未样品接收的数据
        List<EmcOrderTaskBean> unLoggedList = orderTaskOrgList.stream().filter(task -> !StringUtils.equals("logged", task.getStatus())).collect(Collectors.toList());
        List<Long> unLoggedOrderIdList = unLoggedList.stream().map(EmcOrderTaskBean::getOrderId).collect(Collectors.toList());
        //未样品接收样品
        List<EmcOrderBean> unLoggedOrderList =  orderService.getDao().selectListByOneColumnValues(unLoggedOrderIdList, "ID");
        if(!unLoggedOrderList.isEmpty()){
        	 List<String> messageUnLoggedList = new ArrayList<>();
             Map<Long, List<EmcOrderBean>> newOrderMap = unLoggedOrderList.stream().collect(Collectors.groupingBy(o ->o.getId()));
             newOrderMap.forEach((id, unloggedAfterList) -> {
                 String  unloggedOrderNo= unloggedAfterList.stream().map(EmcOrderBean :: getOrderNo).distinct().collect(Collectors.joining("、"));
                 String messageUnLogged = I18nHelper.getMessage("EMC.MODULE.BP.RESULT.RUNS.TIP.NOT_RECEIVED_SAMPLE", unloggedOrderNo);
                 messageUnLoggedList.add(messageUnLogged);
             });   
             String unLoggedNotice= String.join("\n", messageUnLoggedList);    
                 this.addConstraintViolation(unLoggedNotice);
                 return false;
         }
        // 按testid进行分组
        List<String> messageTestList = new ArrayList<>();
        Map<Long, List<EmcOrderTaskBean>> newTestMap = orderTaskOrgList.stream().collect(Collectors.groupingBy(o ->o.getTestId()));
        newTestMap.forEach((testId, orderTestAfterList) -> {
            String  analysisOrgname= orderTestAfterList.stream().map(EmcOrderTaskBean ::getAnalysisOrgName).distinct().collect(Collectors.joining("、"));
            String  testName= orderTestAfterList.stream().map(EmcOrderTaskBean::getTestName).distinct().collect(Collectors.joining("、"));
            String messageTest = I18nHelper.getMessage("EMC.MODULE.BP.RESULT.RUNS.TIP.EXIST_UNFINISHED_ORDER_TASK", analysisOrgname,testName);
            messageTestList.add(messageTest);
        });   
        String noticeTest= String.join("\n", messageTestList);    
        if (!noRunOrderTaskList.isEmpty()) {
            this.addConstraintViolation(noticeTest);
            return false;
        }
   
        // 过滤出已经添加了批的样品测试
        List<String> runIdList = validOrderTaskList.stream().filter(task -> !StringUtils.isEmpty(task.getRunId())).map(EmcOrderTaskBean::getRunId).collect(Collectors.toList());
        List<EmcRunBean> runList = runService.getDao().selectListByOneColumnValues(runIdList, "ID", Arrays.asList("ID", "CHECKSTATUS"));
        //找到属于这个runidlist里面状态不为1的id，更具runid去找到科室名称和测试
        List<EmcRunBean> runListOrg = runService.getDao().selectListByOneColumnValues(runIdList, "ID");

        List<EmcRunBean> runListNew = runListOrg.stream().filter(run -> !StringUtils.equals(Constant.YES, run.getCheckStatus())).collect(Collectors.toList());
     
        List<String> messageList = new ArrayList<>();
        Map<String, List<EmcRunBean>> newMap = runListNew.stream().collect(Collectors.groupingBy(r -> r.getId()));
        newMap.forEach((id, runAfterList) -> {
            String  testName= runAfterList.stream().map(EmcRunBean::getTestName).distinct().collect(Collectors.joining("、"));
            String  createdByOrgName= runAfterList.stream().map(EmcRunBean::getCreatedByOrgName).distinct().collect(Collectors.joining("、"));
            String message = I18nHelper.getMessage("EMC.MODULE.BP.RESULT.RUNS.TIP.EXIST_UNFINISHED_RUN", createdByOrgName,id,testName);
            messageList.add(message);
        });
        String notice= String.join("\n", messageList);    
        List<String> checkStatusList = runList.stream().map(EmcRunBean::getCheckStatus).collect(Collectors.toList());
        //批次的checkStatus有一个不为1即弹出提示
        boolean unchecked = checkStatusList.stream().anyMatch(checkStatus -> !StringUtils.equals(checkStatus, Constant.YES));
        if (unchecked) {
/*            this.addConstraintViolation("EMC.MODULE.BP.RESULT.RUNS.TIP.EXIST_UNFINISHED_RUN");
*/           
        	this.addConstraintViolation(notice);
        	return false;
        }

        return true;
    }

}
