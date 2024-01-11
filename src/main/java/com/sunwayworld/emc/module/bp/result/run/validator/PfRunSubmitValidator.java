package com.sunwayworld.emc.module.bp.result.run.validator;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.emc.module.bp.sample.sample.service.EmcSampleService;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

@GikamBean
@Repository
public class PfRunSubmitValidator extends EmcRunSubmitValidator implements DataValidator {

    @Autowired
    private EmcOrderTaskService orderTaskService;

    @Autowired
    private EmcOrderTaskResultService orderTaskResultService;

    @Autowired
    private EmcOrderService orderService;

    @Autowired
    private EmcSampleService sampleService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        List<EmcRunBean> runList = wrapper.parse(EmcRunBean.class);
        List<String> runIdList = runList.stream().filter(r -> !StringUtils.isEmpty(r.getId())).map(EmcRunBean::getId).collect(Collectors.toList());

        // 筛选状态为logged记录
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(runIdList, "RUNID").stream().filter(e -> "logged".equals(e.getStatus()))
                .collect(Collectors.toList());
        if (orderTaskList.isEmpty()) {
            this.addConstraintViolation("EMC.MODULE.BP.RESULT.RUNS.TIP.EMPTY_SAMPLE");
            return false;
        }

        List<Long> orderTaskIdList = orderTaskList.stream().map(EmcOrderTaskBean::getId).distinct().collect(Collectors.toList());

        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "ORDERTASKID");
        //获取报出结果为空的样品编号，反应给前端
        List<EmcOrderTaskResultBean> orderTaskResultListEmpty = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "ORDERTASKID")
        		.stream().filter(e -> StringUtils.isEmpty(e.getReportResult())).collect(Collectors.toList());

        // 按样品编号进行分组
        List<String> messageList = new ArrayList<>();
        Map<String, List<EmcOrderTaskResultBean>> orderTaskResultMap = orderTaskResultListEmpty.stream().collect(Collectors.groupingBy(result -> result.getOrderNo()));
        orderTaskResultMap.forEach((orderNo, resultList) -> {
            String message = I18nHelper.getMessage("EMC.MODULE.BP.RESULT.RUNS.TIP.EMPTY_REPORT_RESULT", orderNo);
            messageList.add(message);
        });
        String notice= String.join("\n\n", messageList);

        if (orderTaskResultList.isEmpty()) {
            this.addConstraintViolation("EMC.MODULE.BP.RESULT.RUNS.TIP.EMPTY_RESULT");
            return false;
        }

        if (orderTaskResultList.stream().anyMatch(e -> StringUtils.isEmpty(e.getReportResult()))) {
            this.addConstraintViolation(notice);
            return false;
        }
        //提交的时候;限制普通样品干物质含量不能>=100
        List<EmcOrderTaskResultBean> orderTaskResultNeedCheckBeans = new ArrayList<>();
        //干物质含量
        List<EmcOrderTaskResultBean> orderTaskResultGanBeans = orderTaskResultList.stream().filter(res ->StringUtils.equals(PfConstant.DRY_MATTER_CONCENT_I,res.getItemName()))
                .filter(res ->!StringUtils.equals(EmcConstant.QCCATEGORY_QCCATEGORYID_QC_STD,res.getQcCategoryCode())).collect(Collectors.toList());
        //如果有值超过了100的，加到orderTaskResultNeedCheckBeans
        orderTaskResultGanBeans.forEach(res ->{
            if(NumberUtils.isNumber(res.getReportResult())){
                Double doubleString = Double.parseDouble(res.getReportResult());
                if(doubleString>=100){
                    orderTaskResultNeedCheckBeans.add(res);
                }
            }
        });

        if(!orderTaskResultNeedCheckBeans.isEmpty()){
            // 按样品编号进行分组
            List<String> message2List = new ArrayList<>();
            Map<String, List<EmcOrderTaskResultBean>> orderTaskResultCheckMap = orderTaskResultNeedCheckBeans.stream().collect(Collectors.groupingBy(result -> result.getOrderNo()));
            orderTaskResultCheckMap.forEach((orderNo, resultList) -> {
                String message2 = I18nHelper.getMessage("EMC.MODULE.BP.RESULT.RUNS.TIP.OVER_SPECIAL_REPORT_RESULT", orderNo);
                message2List.add(message2);
            });
            String notice2= String.join("\n\n", message2List);
            this.addConstraintViolation(notice2);
            return false;
        }

        // 批下实验室标准样品，如果没有填写标物编号，不让提交
        List<EmcOrderTaskBean> emptyStockBatchNoTaskList = orderTaskList.stream().filter(task -> PfConstant.QCCATEGORY_QCCATEGORYID_QC_STD.equals(task.getQcCategoryCode()) && StringUtils.isEmpty(task.getStockBatchNo())).collect(Collectors.toList());
        if (!emptyStockBatchNoTaskList.isEmpty()) {
            this.addConstraintViolation("EMC.MODULE.BP.RESULT.RUNS.TIP.STOCK_BATCH_NO_IS_EMPTY");
            return false;
        }

        // 批下样品关联的采样单如果未审核，不允许提交批
        List<Long> orderIdList = orderTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(orderIdList, "id", Arrays.asList("id", "sampleId", "orderNo"));
        List<Long> sampleIdList = orderList.stream().filter(order -> !ObjectUtils.isEmpty(order.getSampleId())).map(EmcOrderBean::getSampleId).distinct().collect(Collectors.toList());
        List<EmcSampleBean> sampleList = sampleService.getDao().selectListByOneColumnValues(sampleIdList, "id", Arrays.asList("id", "status"));

        List<Long> unDoneSampleIdList = sampleList.stream().filter(sample -> !StringUtils.equals("done", sample.getStatus())).map(EmcSampleBean::getId).collect(Collectors.toList());
        if (!unDoneSampleIdList.isEmpty()) {
            String orderNos = orderList.stream().filter(order -> unDoneSampleIdList.contains(order.getSampleId())).map(EmcOrderBean::getOrderNo).collect(Collectors.joining(","));
            String message = I18nHelper.getMessage("EMC.MODULE.BP.RESULT.RUNS.TIP.EXIST_NOT_AUDIT_SAMPLE", orderNos);
            this.addConstraintViolation(message);
            return false;
        }

        return true;
    }

}
