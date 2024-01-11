package com.sunwayworld.emc.module.bp.sample.sample.validator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcFolderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.emc.module.bp.sample.sample.service.EmcSampleService;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

@Repository
public class PfSampleSubmitValidator implements DataValidator {

    @Autowired
    private EmcOrderService orderService;

    @Autowired
    private EmcOrderTaskService orderTaskService;

    @Autowired
    private EmcOrderTaskResultService orderTaskResultService;

    @Autowired
    private EmcFolderService folderService;

    @Autowired
    private EmcSampleService sampleService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        List<EmcSampleBean> sampleList = wrapper.parse(EmcSampleBean.class);

        // 1、验证是否有数据
        List<Long> sampleIdList = sampleList.stream().map(EmcSampleBean::getId).collect(Collectors.toList());
        List<EmcOrderBean> orderList = orderService.selectListByFilter(SearchFilter.instance().match("SAMPLEID", sampleIdList).filter(MatchPattern.OR));
        if (orderList.isEmpty()) {
            this.addConstraintViolation("EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.NOT_ORDER_DATA");
            return false;
        }

        // 过滤终止的样品
        List<EmcOrderBean> normalOrderList = orderList.stream().filter(f -> !Constant.YES.equals(f.getTermination())).collect(Collectors.toList());
        List<Long> orderIdList = normalOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        if (!orderIdList.isEmpty()) {
            String firstParamPointResult = "样品编号: ";
            String secondParamPointResult = "样品编号: ";
            // 筛选指派仪器信息 -- 分析人，仪器不能为空
            // 过滤出现场空白的样品测试，对于现场空白样品测试不需要校验仪器
            // 过滤出已经分包的样品测试，对于已分包的样品测试不需要校验仪器
            List<EmcOrderTaskBean> orderTaskList = orderTaskService.selectListByFilter(SearchFilter.instance().match("ORDERID", orderIdList).filter(MatchPattern.OR)
                    .match("ANALYSISTYPE", "sampling").filter(MatchPattern.SEQ)
                    .match("SUBCONTRACTORID", null).filter(MatchPattern.SEQ)
                    .match("QCCATEGORYCODE", "P-BLANK").filter(MatchPattern.DIFFER), wrapper);

            // ---------------------------------指标仪器前台提示数据 --------------------------start
            String equiptId = "";
            String analysisUserId = "";

            String equiptIdPoint = "";
            String analysisUserIdPoint = "";
            for (int i = 0; i < orderTaskList.size(); i++) {

                if (orderTaskList.get(i).getEquiptId() == null) {
                    equiptId += orderTaskList.get(i).getExt$Item("ORDERNO") + ",";
                    equiptIdPoint = "仪器未选择 ";
                }
                if (orderTaskList.get(i).getAnalysisUserId() == null) {
                    analysisUserId += orderTaskList.get(i).getExt$Item("ORDERNO") + ",";
                    analysisUserIdPoint = "分析人未选择 ";
                }
            }
            if (!StringUtils.isEmpty(equiptIdPoint)) {
                firstParamPointResult += equiptId + equiptIdPoint + "</br>";
            }
            if (!StringUtils.isEmpty(equiptIdPoint)) {
                secondParamPointResult += analysisUserId + analysisUserIdPoint + "</br>";
            }
            // ---------------------------------指标仪器前台提示数据 --------------------------
            if (orderTaskList.stream().anyMatch(e -> (ObjectUtils.isEmpty(e.getEquiptId())) || StringUtils.isEmpty(e.getAnalysisUserId()))) {

                if (firstParamPointResult.length() > 6 && secondParamPointResult.length() > 6 ) {
                    this.addConstraintViolation(firstParamPointResult + secondParamPointResult);
                } else if (firstParamPointResult.length() == 6) {
                    this.addConstraintViolation(secondParamPointResult);
                } else if (secondParamPointResult.length() == 6) {
                    this.addConstraintViolation(firstParamPointResult);
                }
                return false;
            }

            // 筛选结果信息 -- 原始结果不能为空
            if (!orderTaskList.isEmpty()) {
                List<Long> orderTaskIdList = orderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
                List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.selectListByFilter(SearchFilter.instance().match("ORDERTASKID", orderTaskIdList).filter(MatchPattern.OR)
                        .match("ANALYSISTYPE", "sampling").filter(MatchPattern.SEQ), wrapper);

                String reportResult = "";

                String reportResultPoint = "";
                for (int i = 0; i < orderTaskResultList.size(); i++) {

                    if (orderTaskResultList.get(i).getReportResult() == null) {
                        reportResult += orderTaskResultList.get(i).getOrderNo() + ",";
                        reportResultPoint = "原始结果未填写 ";
                    }

                }
                if (!StringUtils.isEmpty(reportResultPoint)) {
                    // 过滤重复的样品编号
                    String[] array = reportResult.trim().split(",");
                    List<String> list = Arrays.asList(array);
                    Set<String> set = new HashSet<>(list);
                    String[] rid = set.toArray(new String[0]);
                    firstParamPointResult += StringUtils.join(rid, ",") + reportResultPoint + "</br>";
                }

                if (orderTaskResultList.stream().anyMatch(e -> (StringUtils.isEmpty(e.getReportResult())))) {
                    this.addConstraintViolation(firstParamPointResult);
                    return false;
                }
            }
        }

        // 2、验证不同点位同一采样时间段存在人员一样的情况，或不同点位同一采样时间段存在仪器一样的情况
        //List<Long> orderIdList = orderList.stream().map(a -> a.getId()).collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.selectListByFilter(SearchFilter.instance().match("ORDERID", orderIdList).filter(MatchPattern.OR).match("ANALYSISTYPE", "sampling").filter(MatchPattern.SEQ));

        List<Long> folderIdList = orderList.stream().map(a -> a.getFolderId()).distinct().collect(Collectors.toList());
        List<EmcFolderBean> folderList = folderService.selectListByIds(folderIdList);

        // 所有在现场采样-数据录入中的采样单
        List<EmcSampleBean> allSampleList = sampleService.selectListByFilter(SearchFilter.instance().match("STATUS", EmcBizConstant.SAMPLE_STATUS_DRAFT).filter(MatchPattern.EQ));
        List<Long> allSampleIdList = allSampleList.stream().map(b -> b.getId()).collect(Collectors.toList());
        if(allSampleIdList.isEmpty()) {
            return true;
        }
        List<EmcOrderBean> allOrderList = orderService.selectListByFilter(SearchFilter.instance().match("SAMPLEID", allSampleIdList).filter(MatchPattern.OR));
        //List<Long> allOrderIdList = allOrderList.stream().map(a -> a.getId()).collect(Collectors.toList());
        //List<EmcOrderTaskBean> allOrderTaskList = orderTaskService.selectListByFilter(SearchFilter.instance().match("ORDERID", allOrderIdList).filter(MatchPattern.OR).match("ANALYSISTYPE", "sampling").filter(MatchPattern.SEQ));

        for (EmcFolderBean f : folderList) {
            // 提交数据点位下的order数据
            List<EmcOrderBean> filterOrderList = orderList.stream().filter(a -> ObjectUtils.equals(a.getFolderId(), f.getId())).collect(Collectors.toList());

            for(EmcOrderBean o : filterOrderList) {

                @SuppressWarnings("unused")
				List<EmcOrderTaskBean> filterOrderTaskList = orderTaskList.stream().filter(a -> ObjectUtils.equals(o.getId(), a.getOrderId())).collect(Collectors.toList());

                for(EmcOrderBean a : allOrderList) {

                    if(ObjectUtils.equals(f.getId(), a.getFolderId())) {
                        continue;
                    }
                    // 比较同一时间段是否有人被安排到两个点位采样
                    /*if(getUserConflict(o, a)) {

                        this.addConstraintViolation(I18nHelper.getMessage("EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.USER_CONFLICT", o.getExt$Item("sampleusername"), a.getExt$Item("folderno")));
                        return false;
                    }

                    // 比较同一时间段是否有仪器被安排到两个点位采样
                    List<EmcOrderTaskBean> filterAllOrderTaskList = allOrderTaskList.stream().filter(b -> ObjectUtils.equals(o.getId(), b.getOrderId())).collect(Collectors.toList());
                    if(getEquiptConflict(o, a, filterOrderTaskList, filterAllOrderTaskList)) {
                        this.addConstraintViolation(I18nHelper.getMessage("EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.USER_CONFLICT", o.getExt$Item("sampleusername"), a.getExt$Item("folderno")));
                        return false;
                    }*/
                }

            }
        }

        return true;
    }

    private LocalDateTime getDateTime(LocalDate date, String time, boolean beginTime) {
        String timeStr = StringUtils.isEmpty(time) ? (beginTime ? "00:00:00" : "23:59:59") : time;
        String dateTime = (date == null) ? LocalDate.now().toString() : date.toString() + " " + timeStr;
        return DateTimeUtils.parseLocalDateTime(dateTime);
    }

    /**
     *
     * @param first
     * @param second
     * @return 比较两个order采样的人员时间是否冲突
     */
    @SuppressWarnings("unused")
	private boolean getUserConflict(EmcOrderBean first, EmcOrderBean second) {
        String firstUsers = first.getExt$Item("sampleuserid");
        String secondUsers = second.getExt$Item("sampleuserid");
        if (StringUtils.isEmpty(firstUsers) || StringUtils.isEmpty(secondUsers)) {
            return false;
        }
        List<String> firstList = Arrays.asList(firstUsers.split(","));
        List<String> secondList = Arrays.asList(secondUsers.split(","));
        if(firstList.stream().noneMatch(a -> secondList.contains(a))) {
            return false;
        }
        LocalDateTime firstBegin = getDateTime(first.getSampleBeginDate(), first.getSampleBeginTime(), true);
        LocalDateTime firstEnd = getDateTime(first.getSampleBeginDate(), first.getSampleBeginTime(), false);

        LocalDateTime secondBegin = getDateTime(second.getSampleBeginDate(), second.getSampleBeginTime(), true);
        LocalDateTime secondEnd = getDateTime(second.getSampleBeginDate(), second.getSampleBeginTime(), false);

        if(firstBegin.isAfter(secondEnd) || firstEnd.isBefore(secondBegin)) {
            return false;
        }
        return true;
    }

    @SuppressWarnings("unused")
	private boolean getEquiptConflict(EmcOrderBean first, EmcOrderBean second, List<EmcOrderTaskBean> firstList, List<EmcOrderTaskBean> secondList) {
        List<Long> firstEquiptIdList = firstList.stream().filter(b -> b != null).map(a -> a.getEquiptId()).distinct().collect(Collectors.toList());
        List<Long> secondEquiptIdList = secondList.stream().filter(b -> b != null).map(a -> a.getEquiptId()).distinct().collect(Collectors.toList());
        if(firstEquiptIdList.stream().noneMatch(a -> secondEquiptIdList.contains(a))) {
            return false;
        }
        LocalDateTime firstBegin = getDateTime(first.getSampleBeginDate(), first.getSampleBeginTime(), true);
        LocalDateTime firstEnd = getDateTime(first.getSampleBeginDate(), first.getSampleBeginTime(), false);

        LocalDateTime secondBegin = getDateTime(second.getSampleBeginDate(), second.getSampleBeginTime(), true);
        LocalDateTime secondEnd = getDateTime(second.getSampleBeginDate(), second.getSampleBeginTime(), false);

        if(firstBegin.isAfter(secondEnd) || firstEnd.isBefore(secondBegin)) {
            return false;
        }
        return true;
    }

}
