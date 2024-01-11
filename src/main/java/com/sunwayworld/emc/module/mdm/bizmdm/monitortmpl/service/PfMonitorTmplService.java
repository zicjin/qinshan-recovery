package com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service;

import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorSchemeSamplingCostBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author zhengxy@sunwayworld.com 2021年10月21日
 */
public interface PfMonitorTmplService extends EmcMonitorTmplService {

    void copySchemeTo(RestJsonWrapperBean wrapper);

    void enableScheme(RestJsonWrapperBean wrapper);

    void disableScheme(RestJsonWrapperBean wrapper);

    void copyAllMonitorToOrg(RestJsonWrapperBean wrapper);

    void editSchemesName(RestJsonWrapperBean wrapper);

    Page<EmcMonitorSchemeSamplingCostBean> selectSchemeSamplingCostPagination(RestJsonWrapperBean wrapper);

    void insertSamplingCost(RestJsonWrapperBean wrapper);

    void deleteSamplingCost(RestJsonWrapperBean wrapper);

    void syncPriceInfoTo(RestJsonWrapperBean wrapper);

    Long copyToSamplingCost(RestJsonWrapperBean wrapper);
}
