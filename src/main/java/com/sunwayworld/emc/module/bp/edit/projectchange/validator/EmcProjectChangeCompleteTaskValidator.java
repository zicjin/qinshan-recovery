package com.sunwayworld.emc.module.bp.edit.projectchange.validator;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcProjectChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.service.EmcProjectChangeService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

/**
 * @author zhengxy@sunwayworld.com 2023年10月19日
 */
@Repository
public class EmcProjectChangeCompleteTaskValidator implements DataValidator {

    @Autowired
    private EmcProjectChangeService projectChangeService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        List<EmcProjectChangeBean> targetProjectChangeList = wrapper.parse(EmcProjectChangeBean.class);
        List<Long> targetProjectChangeIdList = targetProjectChangeList.stream().map(EmcProjectChangeBean::getId).collect(Collectors.toList());

        List<EmcProjectChangeBean> projectChangeList = projectChangeService.selectListByIds(targetProjectChangeIdList);

        // 判定折扣审核状态是否已通过
        boolean invalidDiscountReview = projectChangeList.stream().anyMatch(pc -> Objects.isNull(pc.getDiscountReviewStatus()) || pc.getDiscountReviewStatus() != 1);
        if (invalidDiscountReview) {
            this.addConstraintViolation("存在折扣审核未通过的项目，不允许提交");
            return false;
        }

        return true;
    }
}
