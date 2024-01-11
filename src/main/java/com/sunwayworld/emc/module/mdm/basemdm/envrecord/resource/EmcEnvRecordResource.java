package com.sunwayworld.emc.module.mdm.basemdm.envrecord.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.basemdm.envrecord.bean.EmcEnvRecordBean;
import com.sunwayworld.emc.module.mdm.basemdm.envrecord.service.EmcEnvRecordService;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;

@RequestMapping("/secure/emc/module/mdm/basemdm/env-records")
public interface EmcEnvRecordResource extends GenericResource<EmcEnvRecordService, EmcEnvRecordBean, String>, GenericChoosableResource<EmcEnvRecordService, EmcEnvRecordBean, String> {

}
