package com.sunwayworld.emc.module.mdm.file.fileapply.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.file.fileapply.resource.EmcFileApplyResource;
import com.sunwayworld.emc.module.mdm.file.fileapply.service.EmcFileApplyService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule(value = "文件申请")
@RestController
@GikamBean
public class EmcFileApplyResourceImpl implements EmcFileApplyResource {

    @Autowired
    private EmcFileApplyService fileApplyService;

    @Override
    public EmcFileApplyService getService() {
        return fileApplyService;
    }

}
