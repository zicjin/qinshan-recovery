package com.sunwayworld.emc.module.bp.info.infotmpl.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-19
 */

@RequestMapping("/secure/emc/module/bp/info/info-tmpls/page")
public interface EmcInfoTmplController extends GenericEditListPageController {
    
    public String fieldDetailPage();
    
    public String tmplPointPage();
}
