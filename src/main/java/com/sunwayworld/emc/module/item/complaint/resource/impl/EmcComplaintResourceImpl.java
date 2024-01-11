package com.sunwayworld.emc.module.item.complaint.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.item.complaint.resource.EmcComplaintResource;
import com.sunwayworld.emc.module.item.complaint.service.EmcComplaintService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author guok@sunwayworld.com
 * @date 2019-12-13
 */

@LogModule("申投诉申请")
@RestController
@GikamBean
public class EmcComplaintResourceImpl implements EmcComplaintResource {

    @Autowired
    private EmcComplaintService complaintService;

    @Override
    public EmcComplaintService getService() {
        return complaintService;
    }
}
