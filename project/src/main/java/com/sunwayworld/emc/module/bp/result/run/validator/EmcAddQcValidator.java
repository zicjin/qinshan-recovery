package com.sunwayworld.emc.module.bp.result.run.validator;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.bean.EmcQcCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.service.EmcQcCategoryService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

@Repository
public class EmcAddQcValidator implements DataValidator{

    
    @Autowired
    private EmcQcCategoryService qcCategoryService;
    
    @Autowired
    private EmcOrderTaskService orderTaskService;
    
    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        
        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        List<Long> orderTaskIdList = orderTaskList.stream().map(e -> e.getId()).collect(Collectors.toList());
        // 获取选中数据关联的qc类型
        List<EmcOrderTaskBean> selectedOrderTaskList = orderTaskService.selectListByIds(orderTaskIdList);
        List<Long> qcIdList = selectedOrderTaskList.stream().filter(a -> a.getQcCategoryId() != null).map(e -> e.getQcCategoryId()).distinct().collect(Collectors.toList());
        List<EmcQcCategoryBean> qcCategoryList = qcCategoryService.selectListByIds(qcIdList);
        // 对于qc样，只有 qc类型能否添加质控为1时才能添加qc
        if (qcCategoryList.stream().anyMatch(e -> !"1".equals(e.getAddQc()))) {
            this.addConstraintViolation("EMC.MODULE.BP.RESULT.RUNS.TIP.CAN_NOT_ADD");
            return false;
        }
        return true;
    }
}
