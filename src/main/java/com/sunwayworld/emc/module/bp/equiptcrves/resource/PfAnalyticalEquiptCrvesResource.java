package com.sunwayworld.emc.module.bp.equiptcrves.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.equiptcrves.bean.PfAnalyticalEquiptCrvesBean;
import com.sunwayworld.emc.module.bp.equiptcrves.service.PfAnalyticalEquiptCrvesService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
 * @author licm@sunwayworld.com 2021年10月21日
 */
@RequestMapping("/secure/emc/module/bp/equipt-crves")
public interface PfAnalyticalEquiptCrvesResource extends GenericResource<PfAnalyticalEquiptCrvesService, PfAnalyticalEquiptCrvesBean, Long> {

    void copyCrves(RestJsonWrapperBean wrapper);

    void copyNowCrves(RestJsonWrapperBean wrapper);

    void copyConfigCrves(RestJsonWrapperBean wrapper);

    void insertConfigCurve(RestJsonWrapperBean wrapper);

    String selectRunTestId(RestJsonWrapperBean wrapper);

    String CompareEndDate(RestJsonWrapperBean wrapper);

    String setDefaultCurve(RestJsonWrapperBean wrapper);
    
    String selectItemGroup(RestJsonWrapperBean wrapper);
    
    void changSolId(RestJsonWrapperBean wrapper);


}
