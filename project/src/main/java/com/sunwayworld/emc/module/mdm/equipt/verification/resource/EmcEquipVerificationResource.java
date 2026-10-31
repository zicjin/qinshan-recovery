package com.sunwayworld.emc.module.mdm.equipt.verification.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.equipt.verification.bean.EmcEquipVerificationBean;
import com.sunwayworld.emc.module.mdm.equipt.verification.service.EmcEquipVerificationService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/mdm/equipt/equip-verifications")
public interface EmcEquipVerificationResource extends GenericResource<EmcEquipVerificationService, EmcEquipVerificationBean, Long>, GenericAuditableResource<EmcEquipVerificationService, EmcEquipVerificationBean, Long> {

    void updateRecordPlanOnPass(RestJsonWrapperBean wrapperBean);
}
