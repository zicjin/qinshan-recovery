package com.sunwayworld.emc.module.bp.order.orderretain.resource.impl;



import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.sunwayworld.emc.module.bp.order.orderretain.resource.PfOrderRetainResource;
import com.sunwayworld.emc.module.bp.order.orderretain.service.PfOrderRetainService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
* @author licm@sunwayworld.com 2021年10月15日
*/
@GikamBean
@RestController
@LogModule(value = "留样")
public class PfOrderRetainResourceImpl  extends EmcOrderRetainResourceImpl implements PfOrderRetainResource {

    @Autowired
    private PfOrderRetainService orderRetainService;

    @Override
    @Log(value = "查询模板组", type = LogType.SELECT)
    @RequestMapping(value = "/action/confirm", method = RequestMethod.POST)
    public void confirmRetainOrder(RestJsonWrapperBean wrapper) {
    	orderRetainService.confirmRetainOrder(wrapper);
 	
    }


}
