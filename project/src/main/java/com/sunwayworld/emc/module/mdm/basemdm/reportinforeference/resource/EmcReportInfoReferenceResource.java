package com.sunwayworld.emc.module.mdm.basemdm.reportinforeference.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.basemdm.reportinforeference.bean.EmcReportInfoReferenceBean;
import com.sunwayworld.emc.module.mdm.basemdm.reportinforeference.service.EmcReportInfoReferenceService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
 * @author zhengxy@sunwayworld.com 2020年03月06日
 */
@RequestMapping("/secure/emc/module/mdm/basemdm/report-info-references")
public interface EmcReportInfoReferenceResource extends GenericResource<EmcReportInfoReferenceService, EmcReportInfoReferenceBean, Long> {

    void copy(RestJsonWrapperBean wrapper);
}
