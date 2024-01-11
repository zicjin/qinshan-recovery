package com.sunwayworld.emc.module.mdm.basemdm.script.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.basemdm.script.bean.EmcScriptBean;
import com.sunwayworld.emc.module.mdm.basemdm.script.service.EmcScriptService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/mdm/basemdm/scripts")
public interface EmcScriptResource extends GenericResource<EmcScriptService, EmcScriptBean, String> {

    String insert(RestJsonWrapperBean wrapper);

}
