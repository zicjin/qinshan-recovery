package com.sunwayworld.emc.module.mdm.bizmdm.capacity.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public interface PfTestCapacityController extends EmcTestCapacityController {

    @RequestMapping("/method-param-detail")
    String testCapacityMethodParamDetailPage();

    @RequestMapping("/test-capacity-method-update-param-batch-detail")
    String testCapacityUpdateParamBatchPage();

    @RequestMapping("/capacity-item-group-choose-list")
    String capacityItemGroupChoosePage();

    @RequestMapping("/test-capacity-choose-list")
    String testCapacityChoosePage();    
    
}
