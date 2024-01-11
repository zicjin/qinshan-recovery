package com.sunwayworld.emc.module.bp.project.projscheme.validator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjOrderBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

/**
 * @author zhengxy@sunwayworld.com 2022年07月28日
 */
@Repository
public class PfSyncTestFromSchemeValidator implements DataValidator {

    @Autowired
    private EmcOrderTaskService orderTaskService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class, 1);
        List<EmcProjOrderBean> newSchemeOrderList = wrapper.parse(EmcProjOrderBean.class, 3);

        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskBean> existedTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderIdList, "orderId", Arrays.asList("id", "testId"));
        List<Long> existedTestIdList = existedTaskList.stream().map(EmcOrderTaskBean::getTestId).distinct().collect(Collectors.toList());

        List<Long> newTestIdList = newSchemeOrderList.stream().map(EmcProjOrderBean::getTestId).distinct().collect(Collectors.toList());
        boolean existed = existedTestIdList.stream().anyMatch(newTestIdList::contains);
        if (existed) {
            this.addConstraintViolation("EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TIP.CANNOT_SYNC_NEW_TEST_FROM_SCHEME");
            return false;
        }

        return true;
    }
}
