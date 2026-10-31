package com.sunwayworld.emc.module.bp.bp.validator;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

/**
 * @author zhengxy@sunwayworld.com 2021年11月03日
 */
@Repository
public class PfDeleteItemGroupValidator implements DataValidator {

    @Autowired
    private EmcOrderTaskService orderTaskService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        List<EmcOrderTaskBean> delOrderTaskList = wrapper.parse(EmcOrderTaskBean.class);

        List<EmcOrderTaskBean> orderTaskList = orderTaskService.selectListByFilter(SearchFilter.instance().match("orderId", delOrderTaskList.get(0).getOrderId()).filter(MatchPattern.EQ)
                .match("analysisType", EmcBizConstant.ANALYSISCATEGORY_ANALYSIS).filter(MatchPattern.EQ)
                .match("status", EmcBizConstant.ORDERTASK_STATUS_LOGGED).filter(MatchPattern.EQ));

        List<Long> delOrderTaskIdList = delOrderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskBean> otherOrderTaskList = orderTaskList.stream().filter(task -> !delOrderTaskIdList.contains(task.getId())).collect(Collectors.toList());

        if (otherOrderTaskList.isEmpty()) {
            this.addConstraintViolation("EMC.MODULE.BP.EDIT.CHECKPROJECTS.TIP.CANNOT_DELETE_ALL_LOGGED_TEST");
            return false;
        }

        return true;
    }
}
