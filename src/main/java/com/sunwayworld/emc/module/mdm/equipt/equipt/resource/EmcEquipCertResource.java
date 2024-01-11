package com.sunwayworld.emc.module.mdm.equipt.equipt.resource;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquipCertBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

public interface EmcEquipCertResource extends EmcEquiptResource{

    void insertEquipCert(String equipId, RestJsonWrapperBean wrapper);

    Page<EmcEquipCertBean> selectEquipCertPagationByEquipId(String id, RestJsonWrapperBean wrapper);

    void deleteEquipCert(String id, RestJsonWrapperBean wrapper);

}
