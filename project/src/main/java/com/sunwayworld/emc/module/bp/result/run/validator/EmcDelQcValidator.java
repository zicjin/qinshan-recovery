package com.sunwayworld.emc.module.bp.result.run.validator;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.bean.EmcQcCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.service.EmcQcCategoryService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

@Repository
public class EmcDelQcValidator implements DataValidator{

    
    @Autowired
    private EmcQcCategoryService qcCategoryService;
    
    @Autowired
    private EmcOrderTaskService orderTaskService;
    
    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        
        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        List<Long> orderTaskIdList = orderTaskList.stream().map(e -> e.getId()).collect(Collectors.toList());
        
        
        List<EmcOrderTaskBean> selectedOrderTaskList = orderTaskService.selectListByIds(orderTaskIdList);
        
        if (selectedOrderTaskList.stream().anyMatch(t -> t.getQcCategoryId() == null)) {
            this.addConstraintViolation("EMC.MODULE.BP.RESULT.RUNS.TIP.CAN_NOT_DEL");
            return false;  
        }
        
        List<Long> qcIdList = selectedOrderTaskList.stream().map(e -> e.getQcCategoryId()).distinct().collect(Collectors.toList());
        List<EmcQcCategoryBean> qcCategoryList = qcCategoryService.selectListByIds(qcIdList);
        if (qcCategoryList.stream().anyMatch(e -> !EmcConstant.QCCATEGORY_USECATEGORY_LAB.equals(e.getUseCategory()))) {
            this.addConstraintViolation("EMC.MODULE.BP.RESULT.RUNS.TIP.CAN_NOT_DEL");
            return false;
        }
        return true;
    }
}
