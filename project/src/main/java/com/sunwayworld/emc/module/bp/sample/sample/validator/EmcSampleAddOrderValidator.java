package com.sunwayworld.emc.module.bp.sample.sample.validator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.emc.module.bp.sample.sample.service.EmcSampleService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

/**
 * @author zhengxy@sunwayworld.com 2020年09月13日
 */
@Repository
public class EmcSampleAddOrderValidator implements DataValidator {

    private static final String SOIL_KEYWORD = "土";

    @Autowired
    private EmcSampleService sampleService;

    @Autowired
    private EmcOrderService orderService;

    @Override
    public boolean doValid(Object... args) {
        Object sampleId = args[0];
        EmcSampleBean sample = sampleService.getDao().selectById(NumberUtils.parseLong(sampleId));
        // 如果不是土壤，则不进行校验
        if (!StringUtils.isEmpty(sample.getOrderCategory()) && !StringUtils.contains(sample.getOrderCategory(), SOIL_KEYWORD)) {
            return true;
        }

        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class);

        // 如果是土壤类型采样单，一个采样单只允许添加一个样品
        List<EmcOrderBean> naSoilOrderList = orderList.stream().filter(order -> StringUtils.contains(order.getOrderCategory(), SOIL_KEYWORD) && StringUtils.equals(order.getQcCategoryCode(), EmcConstant.N_A)).collect(Collectors.toList());
        if (!StringUtils.isEmpty(sample.getOrderCategory()) && StringUtils.contains(sample.getOrderCategory(), SOIL_KEYWORD)) {
            List<EmcOrderBean> existedOrderList = orderService.getDao().selectListByOneColumnValue(sampleId, "SAMPLEID", Arrays.asList("ID", "QCCATEGORYCODE"));
            List<EmcOrderBean> existedNaOrderList = existedOrderList.stream().filter(order -> StringUtils.equals(order.getQcCategoryCode(), EmcConstant.N_A)).collect(Collectors.toList());
            if (!existedNaOrderList.isEmpty() && !naSoilOrderList.isEmpty()) {
                this.addConstraintViolation("EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.SOIL_SAMPLE_ONLY_CAN_ADD_ONE_ORDER");
                return false;
            }
        }

        if (!naSoilOrderList.isEmpty() && naSoilOrderList.size() > 1) {
            this.addConstraintViolation("EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.SOIL_SAMPLE_ONLY_CAN_ADD_ONE_ORDER");
            return false;
        }

        return true;
    }
}
