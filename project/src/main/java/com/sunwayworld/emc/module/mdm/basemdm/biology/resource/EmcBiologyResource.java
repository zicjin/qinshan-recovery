package com.sunwayworld.emc.module.mdm.basemdm.biology.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.basemdm.biology.bean.EmcBiologyBean;
import com.sunwayworld.emc.module.mdm.basemdm.biology.service.EmcBiologyService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.tree.resource.GenericTreeResource;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-28
 */
@RequestMapping("/secure/emc/module/mdm/basemdm/biologys")
public interface EmcBiologyResource extends GenericResource<EmcBiologyService, EmcBiologyBean, Long>,GenericTreeResource<EmcBiologyService, EmcBiologyBean, Long> {
        
        @Override
        Long insert(RestJsonWrapperBean wrapper);
        
}
