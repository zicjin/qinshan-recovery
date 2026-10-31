package com.sunwayworld.emc.module.bp.result.run.validator;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

/**
 * @author zhengxy@sunwayworld.com 2021年11月17日
 */
@Repository
public class PfAddRunValidator implements DataValidator {

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class, 0);

        List<Long> methodIdList = orderTaskList.stream().map(EmcOrderTaskBean::getMethodId).distinct().collect(Collectors.toList());
        if (methodIdList.size() > 1) {
            this.addConstraintViolation("EMC.MODULE.BP.RESULT.RUNS.TIP.CAN_NOT_ADD_DIFFERENT_METHOD");
            return false;
        }

        return true;
    }
}
