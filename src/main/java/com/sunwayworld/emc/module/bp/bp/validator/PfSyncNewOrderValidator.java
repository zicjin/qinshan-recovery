package com.sunwayworld.emc.module.bp.bp.validator;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

/**
 * @author zhengxy@sunwayworld.com 2022年06月08日
 */
@Repository
public class PfSyncNewOrderValidator implements DataValidator {

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class);
        List<EmcOrderBean> newOrderList = orderList.stream().filter(order -> StringUtils.equals(order.getExt$Item("syncStatus"), Constant.YES)).collect(Collectors.toList());

        if (newOrderList.isEmpty()) {
            this.addConstraintViolation("EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TIP.CANNOT_SYNC_NEW_ORDER");
            return false;
        }

        return true;
    }
}
