package com.sunwayworld.emc.module.mdm.equipt.equiptcalibrationresult.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.equipt.equiptcalibrationresult.bean.EmcEquiptCaliResultBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcalibrationresult.service.EmcEquiptCaliResultService;
import com.sunwayworld.emc.module.mdm.equipt.equiptcaliresultnorm.bean.EmcEquiptCaliResultNormBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;
/** 
 * @author chenda@sunwayworld.com
 * @date 2020/9/1
 */
@RequestMapping("/secure/emc/module/mdm/equipt/equipt-cali-results")
public interface EmcEquiptCaliResultResource extends GenericResource<EmcEquiptCaliResultService, EmcEquiptCaliResultBean, Long>,
        GenericAuditableResource<EmcEquiptCaliResultService, EmcEquiptCaliResultBean, Long> {

    Long insertTestMethod(Long id, RestJsonWrapperBean wrapper);

    Page<EmcEquiptCaliResultNormBean> selectEquiptTestMethodRawPagination(Long id, RestJsonWrapperBean wrapper);

    void updateRecordPlanOnPass(RestJsonWrapperBean wrapperBean);
}
