package com.sunwayworld.emc.module.mdm.bizmdm.test.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.spring.annotation.GikamBean;



@Controller
@GikamBean
public interface PfTestMethodController extends EmcTestMethodController {
	
    @RequestMapping("/result-order-task/method-param-detail")
    String resultOrderTaskMethodParamDetailPage();
    
    @RequestMapping("/result-order-task/method-param-batch-detail")
    String resultOrderTaskMethodParamBatchPage();

    @RequestMapping("/method-update-param-batch-detail")
    String taskMethodUpdateParamBatchPage();
    
    @RequestMapping("/result-order-task/method-param-batch-order-detail")
    String resultOrderTaskMethodParamBatchOrderPage();
    
    @RequestMapping("/copy-by-category")
    String copyByCategoryPage();

    
}
