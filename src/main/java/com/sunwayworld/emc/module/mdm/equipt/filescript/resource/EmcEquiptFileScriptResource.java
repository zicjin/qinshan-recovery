package com.sunwayworld.emc.module.mdm.equipt.filescript.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.equipt.filescript.bean.EmcEquiptFileScriptBean;
import com.sunwayworld.emc.module.mdm.equipt.filescript.service.EmcEquiptFileScriptService;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;

@RequestMapping("/secure/emc/module/mdm/equipt/file-scripts")
public interface EmcEquiptFileScriptResource
        extends GenericResource<EmcEquiptFileScriptService, EmcEquiptFileScriptBean, Long>, GenericChoosableResource<EmcEquiptFileScriptService, EmcEquiptFileScriptBean, Long> {

}