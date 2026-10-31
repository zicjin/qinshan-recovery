package com.sunwayworld.emc.module.mdm.equipt.equiptcheck.service;

import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.bean.EmcEquipPartBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcEquipPartService extends GenericService<EmcEquipPartBean, Long> {

    Page<EmcEquipPartBean> selectEquiptPartPaginationById(Long id, RestJsonWrapperBean wrapper);

    Long insertEquiptPart(Long id, RestJsonWrapperBean wrapper);
}
