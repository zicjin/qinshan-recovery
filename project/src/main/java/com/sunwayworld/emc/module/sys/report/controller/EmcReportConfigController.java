package com.sunwayworld.emc.module.sys.report.controller;

import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author zhengxy@sunwayworld.com 2019年12月04日
 */
public interface EmcReportConfigController {

    @RequestMapping({"/preview"})
    String previewPage();
}
