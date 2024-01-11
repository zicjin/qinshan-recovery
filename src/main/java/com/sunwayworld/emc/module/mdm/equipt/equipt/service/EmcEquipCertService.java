package com.sunwayworld.emc.module.mdm.equipt.equipt.service;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquipCertBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcEquipCertService extends GenericService<EmcEquipCertBean, Long> {

    void insertEquipCert(String equipId, RestJsonWrapperBean wrapper);

}
