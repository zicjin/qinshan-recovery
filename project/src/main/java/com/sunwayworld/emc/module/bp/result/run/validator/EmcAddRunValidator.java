package com.sunwayworld.emc.module.bp.result.run.validator;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

/**
 * @author magt 2019年10月24日
 */
@Repository
public class EmcAddRunValidator implements DataValidator {

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class, 0);

        Long firstMethodId = orderTaskList.get(0).getMethodId();
        String firstOrderCategory = orderTaskList.get(0).getOrderCategory();

        // 校验选择的多个批中是否有方法编码或者样品类型不一致的数据
        if (orderTaskList.stream().anyMatch(o -> !ObjectUtils.equals(o.getMethodId(), firstMethodId))) {
            this.addConstraintViolation("EMC.MODULE.BP.RESULT.RUNS.TIP.CAN_NOT_ADD_RUN");
            return false;
        }

        return true;
    }

}
