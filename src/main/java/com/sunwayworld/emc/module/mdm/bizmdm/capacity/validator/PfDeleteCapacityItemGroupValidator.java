package com.sunwayworld.emc.module.mdm.bizmdm.capacity.validator;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemGroupService;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemGroupBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

/**
 * @author zhengxy@sunwayworld.com 2023年07月20日
 */
@Repository
public class PfDeleteCapacityItemGroupValidator implements DataValidator {

    @Autowired
    private EmcOrderItemGroupService orderItemGroupService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        List<EmcCapacityItemGroupBean> capacityItemGroupList = wrapper.parse(EmcCapacityItemGroupBean.class);
        List<Long> capacityItemGroupIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getId).collect(Collectors.toList());

        List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(capacityItemGroupIdList, "itemGroupId", Collections.singletonList("id"));
        if (!orderItemGroupList.isEmpty()) {
            this.addConstraintViolation("EMC.MODULE.MDM.BIZMDM.TEST.CAPACITYS.TIP.CANNOT_DELETE_ITEM_GROUP");
            return false;
        }

        return true;
    }
}
