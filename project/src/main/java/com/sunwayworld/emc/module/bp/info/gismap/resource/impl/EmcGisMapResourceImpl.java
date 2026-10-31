package com.sunwayworld.emc.module.bp.info.gismap.resource.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.info.gismap.resource.EmcGisMapResource;
import com.sunwayworld.emc.module.bp.info.gismap.service.EmcGisMapService;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule("GIS地图")
@RestController
@GikamBean
public class EmcGisMapResourceImpl implements EmcGisMapResource{

    @Autowired
    private EmcGisMapService gisMapService;

    @Override
    public EmcGisMapService getService() {
        return gisMapService;
    }

    @Override
    @Log(value = "GIS地图查询监测点类型", type = LogType.SELECT)
    @RequestMapping(value = "/monitorPointCategories/queries", method = RequestMethod.POST)
    public List<EmcReportResultBean> selectMonitorPointCategories(RestJsonWrapperBean wrapper) {
        return getService().selectMonitorPointCategories(wrapper);
    }
    
    @Override
    @Log(value = "GIS地图查询测试名称", type = LogType.SELECT)
    @RequestMapping(value = "/testNames/queries", method = RequestMethod.POST)
    public List<EmcReportResultBean> selectTestNames(RestJsonWrapperBean wrapper) {
        return getService().selectTestNames(wrapper);
    }
    
    @Override
    @Log(value = "GIS地图查询分析项名称", type = LogType.SELECT)
    @RequestMapping(value = "/itemAliasNames/queries", method = RequestMethod.POST)
    public List<EmcReportResultBean> selectItemAliasNames(RestJsonWrapperBean wrapper) {
        return getService().selectItemAliasNames(wrapper);
    }
}
