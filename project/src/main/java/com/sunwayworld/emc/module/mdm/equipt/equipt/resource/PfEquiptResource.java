package com.sunwayworld.emc.module.mdm.equipt.equipt.resource;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptMaintainRepairBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPerCheckPlanBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

public interface PfEquiptResource extends EmcEquipCertResource {
    Page<EmcEquiptPerCheckPlanBean> selectGlobalPerCheckPlans(RestJsonWrapperBean wrapper);

    Page<EmcEquiptMaintainRepairBean> selectEquiptMaintainRepairPagination(Long id, RestJsonWrapperBean wrapper);

}
