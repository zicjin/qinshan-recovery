package com.sunwayworld.emc.module.bp.sample.sample.validator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcFolderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.emc.module.bp.sample.sample.service.EmcSampleService;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

@Repository
public class EmcSampleSubmitValidator implements DataValidator {

    @Autowired
    private EmcOrderService orderService;

    @Autowired
    private EmcOrderTaskService orderTaskService;

    @Autowired
    private EmcFolderService folderService;

    @Autowired
    private EmcSampleService sampleService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        List<EmcSampleBean> sampleList = wrapper.parse(EmcSampleBean.class);

        // 1、验证是否有数据

        List<Long> sampleIdList = sampleList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcOrderBean> orderList = orderService.selectListByFilter(SearchFilter.instance().match("SAMPLEID", sampleIdList).filter(MatchPattern.OR));
        if (orderList.isEmpty()) {
            this.addConstraintViolation("EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.NOT_ORDER_DATA");
            return false;
        }

        // 2、验证不同点位同一采样时间段存在人员一样的情况，或不同点位同一采样时间段存在仪器一样的情况
        List<Long> orderIdList = orderList.stream().map(a -> a.getId()).collect(Collectors.toList());
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
        List<Long> allOrderIdList = allOrderList.stream().map(a -> a.getId()).collect(Collectors.toList());
        List<EmcOrderTaskBean> allOrderTaskList = orderTaskService.selectListByFilter(SearchFilter.instance().match("ORDERID", allOrderIdList).filter(MatchPattern.OR).match("ANALYSISTYPE", "sampling").filter(MatchPattern.SEQ));

        for (EmcFolderBean f : folderList) {
            // 提交数据点位下的order数据
            List<EmcOrderBean> filterOrderList = orderList.stream().filter(a -> ObjectUtils.equals(a.getFolderId(), f.getId())).collect(Collectors.toList());

            for(EmcOrderBean o : filterOrderList) {

                List<EmcOrderTaskBean> filterOrderTaskList = orderTaskList.stream().filter(a -> ObjectUtils.equals(o.getId(), a.getOrderId())).collect(Collectors.toList());

                for(EmcOrderBean a : allOrderList) {

                    if(ObjectUtils.equals(f.getId(), a.getFolderId())) {
                        continue;
                    }
                    // 比较同一时间段是否有人被安排到两个点位采样
                    if(getUserConflict(o, a)) {

                        this.addConstraintViolation(I18nHelper.getMessage("EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.USER_CONFLICT", o.getExt$Item("sampleusername"), a.getExt$Item("folderno")));
                        return false;
                    }

                    // 比较同一时间段是否有仪器被安排到两个点位采样
                    List<EmcOrderTaskBean> filterAllOrderTaskList = allOrderTaskList.stream().filter(b -> ObjectUtils.equals(o.getId(), b.getOrderId())).collect(Collectors.toList());
                    if(getEquiptConflict(o, a, filterOrderTaskList, filterAllOrderTaskList)) {
                        this.addConstraintViolation(I18nHelper.getMessage("EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.USER_CONFLICT", o.getExt$Item("sampleusername"), a.getExt$Item("folderno")));
                        return false;
                    }
                }

            }
        }

        return true;
    }

    private LocalDateTime getDateTime(LocalDate date, String time, boolean beginTime) {
        String dateTime = Optional.ofNullable(date).orElse(LocalDate.now()).toString() + " " + (StringUtils.isEmpty(time) ? (beginTime ? "00:00:00" : "23:59:59") : time);

        return DateTimeUtils.parseLocalDateTime(dateTime);
    }

    /**
     *
     * @param first
     * @param second
     * @return 比较两个order采样的人员时间是否冲突
     */
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
