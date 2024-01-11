package com.sunwayworld.emc.module.bp.bp.validator;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemGroupService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

/**
 * @author zhengxy@sunwayworld.com 2023年01月31日
 */
@Repository
public class PfEditOrderQcValidator implements DataValidator {

    @Autowired
    private EmcOrderItemGroupService orderItemGroupService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class);
        if(orderList.isEmpty()){
            return true;
        }

        String qcName = orderList.get(0).getQcName();
        boolean containsBlank = StringUtils.contains(qcName, PfConstant.QC_BLANK_KEYWORD);
        if (!containsBlank) {
            return true;
        }

/*        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).distinct().collect(Collectors.toList());
        List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(orderIdList, "orderId");
        boolean matched = orderItemGroupList.stream().anyMatch(group -> StringUtils.contains(group.getItemGroup(), PfConstant.WATER_CONTENT_TEST_MATCH_KEYWORD)
                || StringUtils.contains(group.getItemGroup(), PfConstant.WATER_CONTENT_TEST_MATCH_KEYWORD_2));
        if (matched) {
            this.addConstraintViolation("EMC.MODULE.BP.EDIT.PROJECTSCHEMES.TIP.CANNOT_EDIT_QC_TO_BLANK");
            return false;
        }*/

        return true;
    }
}
