package com.sunwayworld.emc.module.sa.projectquery;

import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/secure/emc/module/sa/project-queries/page")
public interface PfProjectQueryController {

    @RequestMapping("/business-diagram")
    String businessDiagram();
}
