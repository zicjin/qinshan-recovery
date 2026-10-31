package com.sunwayworld.emc.module.bp.info.gismap.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.bp.info.gismap.controller.EmcGisMapController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
/**
 * @author magt@sunwayworld.com
 * @date 2020-02-07
 */

@Controller
@GikamBean
public class EmcGisMapControllerImpl implements EmcGisMapController{

    @Log(value = "gis地图列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/bp/info/gis-maps/emc-gis-map-edit-list";
    }

}
