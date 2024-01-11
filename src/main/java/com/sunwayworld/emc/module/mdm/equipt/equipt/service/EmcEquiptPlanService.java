package com.sunwayworld.emc.module.mdm.equipt.equipt.service;

import java.util.List;

import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.bean.EmcEquiptMaintBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPlanBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-19
 */

public interface EmcEquiptPlanService extends GenericService<EmcEquiptPlanBean, Long> {


    List<EmcEquiptMaintBean> selectMaintSelectableList(Long id, RestJsonWrapperBean wrapper);

    Page<EmcEquiptPlanBean> selectRawPaginationByEquiptId(Long equiptId, RestJsonWrapperBean wrapper);
    
    Page<EmcEquiptPlanBean> selectEquiptMaintPlanOverdue(RestJsonWrapperBean wrapper);

    void updatePlan(EmcEquiptPlanBean emcEquiptPlanBean);

}
