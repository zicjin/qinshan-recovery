package com.sunwayworld.emc.module.mdm.equipt.equiptcalibrationresult.service;

import com.sunwayworld.emc.module.mdm.equipt.equiptcalibrationresult.bean.EmcEquiptCaliResultBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcaliresultnorm.bean.EmcEquiptCaliResultNormBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcEquiptCaliResultService extends GenericService<EmcEquiptCaliResultBean, Long>,
                                                    GenericAuditableService<EmcEquiptCaliResultBean, Long> {
    Long insertTestMethod(Long id, RestJsonWrapperBean wrapper);
    Page<EmcEquiptCaliResultNormBean> selectEquiptTestMethodRawPagination(Long id, RestJsonWrapperBean wrapper);

    void updateEquiptPlanOnPass(RestJsonWrapperBean wrapper);
}
