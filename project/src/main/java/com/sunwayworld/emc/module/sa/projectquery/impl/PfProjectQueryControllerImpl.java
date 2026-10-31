package com.sunwayworld.emc.module.sa.projectquery.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.sa.projectquery.PfProjectQueryController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class PfProjectQueryControllerImpl extends EmcProjectQueryControllerImpl implements PfProjectQueryController {

    @Override
    @Log(value = "业务流程图", type = LogType.CONTROLLER)
    public String businessDiagram() {
        return "emc/module/sa/project-queries/emc-project-business-diagram";
    }
}
