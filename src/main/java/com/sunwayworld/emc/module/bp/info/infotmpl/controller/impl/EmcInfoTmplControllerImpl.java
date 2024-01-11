package com.sunwayworld.emc.module.bp.info.infotmpl.controller.impl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.info.infotmpl.controller.EmcInfoTmplController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-19
 */

@Controller
@GikamBean
public class EmcInfoTmplControllerImpl implements EmcInfoTmplController{

    @Log(value = "上报模板编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/bp/info/info-tmpls/emc-info-tmpl-edit-list";
    }

    @Log(value = "上报模板详情页", type = LogType.CONTROLLER)
    @Override
    @RequestMapping("/field-detail")
    public String fieldDetailPage() {
        return "emc/module/bp/info/info-tmpls/emc-info-tmpl-field-detail";
    }

    @Log(value = "上报模板监测点页", type = LogType.CONTROLLER)
    @Override
    @RequestMapping("/tmpl-point")
    public String tmplPointPage() {
        return "emc/module/bp/info/info-tmpls/emc-info-tmpl-point-edit-list";
    }
}
