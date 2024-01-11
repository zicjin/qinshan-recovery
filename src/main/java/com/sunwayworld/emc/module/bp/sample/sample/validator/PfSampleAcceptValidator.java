package com.sunwayworld.emc.module.bp.sample.sample.validator;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.emc.module.bp.sample.sample.service.EmcSampleService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

/**
 * @author zhengxy@sunwayworld.com 2023年04月10日
 */
@Repository
public class PfSampleAcceptValidator implements DataValidator {

    @Autowired
    private EmcSampleService sampleService;

    @Autowired
    private EmcOrderService orderService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        List<EmcSampleBean> sampleList = wrapper.parse(EmcSampleBean.class);
        List<Long> sampleIdList = sampleList.stream().map(EmcSampleBean::getId).collect(Collectors.toList());

        List<EmcSampleBean> targetSampleList = sampleService.getDao().selectListByOneColumnValues(sampleIdList, "id", Arrays.asList("id", "source", "acceptedDate"));

        List<EmcSampleBean> validSampleList = targetSampleList.stream().filter(sample -> "orderregistration".equals(sample.getSource()) && !ObjectUtils.isEmpty(sample.getAcceptedDate())).collect(Collectors.toList());
        if (validSampleList.isEmpty()) {
            return true;
        }

        List<Long> validSampleIdList = validSampleList.stream().map(EmcSampleBean::getId).collect(Collectors.toList());
        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(validSampleIdList, "sampleId", Arrays.asList("id", "sampleId", "sendDate"));

        boolean beforeSendDate = validSampleList.stream().anyMatch(sample -> orderList.stream().filter(order -> ObjectUtils.equals(order.getSampleId(), sample.getId()) && !ObjectUtils.isEmpty(order.getSendDate())).anyMatch(order -> sample.getAcceptedDate().isBefore(LocalDateTime.of(order.getSendDate(), LocalTime.of(0, 0)))));

        if (beforeSendDate) {
            this.addConstraintViolation("EMC.MODULE.BP.PROJECT.PROJECTS.TIP.ACCEPT_DATE_CAN_NOT_LESS_SEND_DATE");
            return false;
        }

        return true;
    }
}
