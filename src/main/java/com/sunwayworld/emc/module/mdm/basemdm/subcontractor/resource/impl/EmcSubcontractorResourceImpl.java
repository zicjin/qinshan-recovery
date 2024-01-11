package com.sunwayworld.emc.module.mdm.basemdm.subcontractor.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.basemdm.subcontractor.bean.EmcSubcontractRecordBean;
import com.sunwayworld.emc.module.mdm.basemdm.subcontractor.resource.EmcSubcontractorResource;
import com.sunwayworld.emc.module.mdm.basemdm.subcontractor.service.EmcSubcontractorService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author magh@sunwayworld.com
 * @date 2019-11-05
 */

@LogModule("分包商")
@RestController
@GikamBean
public class EmcSubcontractorResourceImpl implements EmcSubcontractorResource {
    @Autowired
    private EmcSubcontractorService emcSubcontractorService;

    @Override
    public EmcSubcontractorService getService() {
        return emcSubcontractorService;
    }

    @Log(value = "根据分包商ID查询分包记录", type = LogType.SELECT)
    @Override
    public Page<EmcSubcontractRecordBean> selectRecordPaginationBySubcontractorId(Long id, RestJsonWrapperBean wrapper) {
        return getService().selectRecordPaginationBySubcontractorId(id, wrapper);
    }

}
