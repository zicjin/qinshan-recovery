package com.sunwayworld.emc.module.bp.result.physchemic.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.result.physchemic.resource.PfPhysChemicResource;
import com.sunwayworld.emc.module.bp.result.physchemic.service.PfPhysChemicService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author licm@sunwayworld.com 2022年07月12日
 */
@LogModule(value = "phys-chemic")
@RestController
@GikamBean
public class PfPhysChemicResourceImpl implements PfPhysChemicResource {

    @Autowired
    private PfPhysChemicService pfPhysChemicService;

    @Override
    public PfPhysChemicService getService() {
        return pfPhysChemicService;
    }

    @Log(value = "添加", type = LogType.INSERT)
	@Override
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
	public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
	}
}
