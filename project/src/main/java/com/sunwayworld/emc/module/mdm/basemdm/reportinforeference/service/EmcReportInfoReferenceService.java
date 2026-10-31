package com.sunwayworld.emc.module.mdm.basemdm.reportinforeference.service;

import com.sunwayworld.emc.module.mdm.basemdm.reportinforeference.bean.EmcReportInfoReferenceBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author zhengxy@sunwayworld.com 2020年03月06日
 */
public interface EmcReportInfoReferenceService extends GenericService<EmcReportInfoReferenceBean, Long> {

    void copy(RestJsonWrapperBean wrapper);
}
