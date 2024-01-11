package com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorSchemeCostBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorSchemeSamplingCostBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@RequestMapping("/secure/emc/module/mdm/bizmdm/monitor-tmpls")
public interface PfMonitorTmplResource extends  EmcMonitorTmplResource{
    Long insertSchemeCost(RestJsonWrapperBean wrapper);

    Page<EmcMonitorSchemeCostBean> selectSchemeCostPaginationByTmplId(String id, RestJsonWrapperBean wrapper);

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
