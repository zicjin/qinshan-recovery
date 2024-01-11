package com.sunwayworld.emc.module.bp.subpackage.validator;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.subpackage.bean.EmcSubpackageBean;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

/**
 * @author zhengxy@sunwayworld.com 2022年09月05日
 */
@Repository
public class EmcAddToSubpackageValidator implements DataValidator {

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        List<EmcSubpackageBean> subpackageList = wrapper.parse(EmcSubpackageBean.class);

        List<EmcSubpackageBean> assignedList = subpackageList.stream().filter(s -> Constant.YES.equals(s.getStatus())).collect(Collectors.toList());
        if (!assignedList.isEmpty()) {
            this.addConstraintViolation("所选分包单已经被委派，无法添加新的测试");
            return false;
        }

        return true;
    }
}
