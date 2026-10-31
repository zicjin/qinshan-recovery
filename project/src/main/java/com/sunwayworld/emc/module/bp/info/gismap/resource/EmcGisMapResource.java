package com.sunwayworld.emc.module.bp.info.gismap.resource;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.info.gismap.service.EmcGisMapService;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@RequestMapping("/secure/emc/module/bp/info/gis-maps")
public interface EmcGisMapResource {

    EmcGisMapService getService();
    
    List<EmcReportResultBean> selectMonitorPointCategories(RestJsonWrapperBean wrapper);
    
    List<EmcReportResultBean> selectTestNames(RestJsonWrapperBean wrapper);
    
    List<EmcReportResultBean> selectItemAliasNames(RestJsonWrapperBean wrapper);
}
