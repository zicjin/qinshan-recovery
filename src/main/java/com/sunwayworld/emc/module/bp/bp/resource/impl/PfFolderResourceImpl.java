package com.sunwayworld.emc.module.bp.bp.resource.impl;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.bp.resource.PfFolderResource;
import com.sunwayworld.emc.module.bp.bp.service.PfFolderService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author licm@sunwayworld.com
 * @date 2021/11/04 
 */
@RestController
@GikamBean
public class PfFolderResourceImpl extends EmcFolderResourceImpl implements PfFolderResource {

    @Autowired
    private PfFolderService pfFolderService;

    @Override
    @Log(value = "提示信息", type = LogType.SELECT)
    @RequestMapping(value = "/action/submit-remind", method = RequestMethod.POST)
    public String submitRemind(RestJsonWrapperBean wrapper) {
    	return pfFolderService.submitRemind(wrapper);
    }

}
