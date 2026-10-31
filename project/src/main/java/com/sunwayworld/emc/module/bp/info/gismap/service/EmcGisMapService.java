package com.sunwayworld.emc.module.bp.info.gismap.service;

import java.util.List;

import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

public interface EmcGisMapService {
    
    List<EmcReportResultBean> selectMonitorPointCategories(RestJsonWrapperBean wrapper);
    
    List<EmcReportResultBean> selectTestNames(RestJsonWrapperBean wrapper);
    
    List<EmcReportResultBean> selectItemAliasNames(RestJsonWrapperBean wrapper);

}
