package com.sunwayworld.emc.module.bp.equiptcrves.service;

import com.sunwayworld.emc.module.bp.equiptcrves.bean.PfAnalyticalEquiptCrvesBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author licm@sunwayworld.com 2021年10月21日
 */
public interface PfAnalyticalEquiptCrvesService extends GenericService<PfAnalyticalEquiptCrvesBean, Long> {

    void copyCrves(RestJsonWrapperBean wrapper);

    void copyNowCrves(RestJsonWrapperBean wrapper);

    void insertConfigCurve(RestJsonWrapperBean wrapper);

    String selectRunTestId(RestJsonWrapperBean wrapper);

    String CompareEndDate(RestJsonWrapperBean wrapper);

    String setDefaultCurve(RestJsonWrapperBean wrapper);
    
    void copyConfigCrves(RestJsonWrapperBean wrapper);

    String selectItemGroup(RestJsonWrapperBean wrapper);
    
    void changSolId(RestJsonWrapperBean wrapper);


    
}
