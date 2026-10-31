package com.sunwayworld.emc.module.mdm.basemdm.fixedasset.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.basemdm.fixedasset.bean.EmcFixedassetBean;
import com.sunwayworld.emc.module.mdm.basemdm.fixedasset.service.EmcFixedassetService;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-25
 */
@RequestMapping("/secure/emc/module/mdm/basemdm/fixedassets")
public interface EmcFixedassetResource extends GenericResource<EmcFixedassetService, EmcFixedassetBean, Long> {
        
        
}
