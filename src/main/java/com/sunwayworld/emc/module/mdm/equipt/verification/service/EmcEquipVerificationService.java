package com.sunwayworld.emc.module.mdm.equipt.verification.service;

import com.sunwayworld.emc.module.mdm.equipt.verification.bean.EmcEquipVerificationBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcEquipVerificationService extends GenericService<EmcEquipVerificationBean, Long>, GenericAuditableService<EmcEquipVerificationBean, Long> {

    void updateEquiptPlanOnPass(RestJsonWrapperBean wrapper);
}
