package com.sunwayworld.emc.module.mdm.equipt.equipt.service;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPerCheckPlanBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.PfEquiptMaintainRepairDTO;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.PfEquiptPerCheckDTO;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.PfEquiptPlanDTO;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.PfEquiptUserDTO;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

public interface PfEquiptService extends EmcEquiptService {

    Page<EmcEquiptPerCheckPlanBean> selectGlobalPerCheckPlans(RestJsonWrapperBean wrapper);
    
	Page<PfEquiptPlanDTO> selectEquiptPlan(RestJsonWrapperBean wrapper);

	Page<PfEquiptPerCheckDTO> selectEquiptPerCheck(RestJsonWrapperBean wrapper);
	
	
	Page<PfEquiptMaintainRepairDTO> selectEquiptMaintainRepair(RestJsonWrapperBean wrapper);
	
	Page<PfEquiptUserDTO> selectEquiptUser(RestJsonWrapperBean wrapper);	

    void updateStatusBySourceDate();

}


