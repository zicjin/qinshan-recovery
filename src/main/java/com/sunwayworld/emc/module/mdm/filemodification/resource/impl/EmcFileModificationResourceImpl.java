package com.sunwayworld.emc.module.mdm.filemodification.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.filemodification.resource.EmcFileModificationResource;
import com.sunwayworld.emc.module.mdm.filemodification.service.EmcFileModificationService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhanghy@sunwayworld.com 2020-09-01
 */
@LogModule("文件修改申请")
@RestController
@GikamBean
public class EmcFileModificationResourceImpl implements EmcFileModificationResource {

	@Autowired
	private EmcFileModificationService emcFileModificationService;
	
	@Override
	public EmcFileModificationService getService() {
		return emcFileModificationService;
	}
}
