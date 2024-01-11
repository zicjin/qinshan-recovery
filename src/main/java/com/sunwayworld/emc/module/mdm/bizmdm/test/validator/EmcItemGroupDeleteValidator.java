package com.sunwayworld.emc.module.mdm.bizmdm.test.validator;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemGroupService;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjOrderBean;
import com.sunwayworld.emc.module.bp.project.projscheme.service.EmcProjOrderService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemGroupBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

/**
 * @author zhengxy@sunwayworld.com 2020年12月14日
 */
@Repository
public class EmcItemGroupDeleteValidator implements DataValidator {

    @Autowired
    private EmcOrderItemGroupService orderItemGroupService;

    @Autowired
    private EmcProjOrderService projOrderService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);

        List<EmcItemGroupBean> itemGroupList = wrapper.parse(EmcItemGroupBean.class);
        List<Long> itemGroupIdList = itemGroupList.stream().map(EmcItemGroupBean::getId).collect(Collectors.toList());
        List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(itemGroupIdList, "ITEMGROUPID", Collections.singletonList("ID"));

        if (!orderItemGroupList.isEmpty()) {
            this.addConstraintViolation("EMC.MODULE.MDM.BIZMDM.TESTS.TIP.CAN_NOT_DELETE_ITEM_GROUP");
            return false;
        }

        List<EmcProjOrderBean> projOrderList = projOrderService.getDao().selectListByOneColumnValues(itemGroupIdList, "ITEMGROUPID", Collections.singletonList("ID"));
        if (!projOrderList.isEmpty()) {
            this.addConstraintViolation("EMC.MODULE.MDM.BIZMDM.TESTS.TIP.CAN_NOT_DELETE_ITEM_GROUP");
            return false;
        }

        return true;
    }
}
