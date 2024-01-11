package com.sunwayworld.emc.module.bp.project.project.validator;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

/**
 * @author yuanhh 2019年10月28日
 */
@Repository
public class EmcUndoProjectValidator implements DataValidator {
    @Autowired
    private EmcOrderService orderService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        List<EmcProjectBean> projectList = wrapper.parse(EmcProjectBean.class);
        List<Long> projectIdList = projectList.stream().map(e -> e.getId()).collect(Collectors.toList());

        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(projectIdList, "PROJID");
        for (EmcOrderBean order : orderList) {
            if (!ObjectUtils.isEmpty(order.getSampleId())) {
                this.addConstraintViolation("EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.UNDO_FAIL");
                return false;
            }
        }
        return true;
    }

}
