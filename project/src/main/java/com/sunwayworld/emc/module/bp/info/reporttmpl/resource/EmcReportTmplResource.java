package com.sunwayworld.emc.module.bp.info.reporttmpl.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.info.reporttmpl.bean.EmcReportTmplBean;
import com.sunwayworld.emc.module.bp.info.reporttmpl.bean.EmcReportTmplLineBean;
import com.sunwayworld.emc.module.bp.info.reporttmpl.service.EmcReportTmplService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.resource.GenericActivatableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;

@RequestMapping("/secure/emc/module/bp/info/report-tmpls")
public interface EmcReportTmplResource extends GenericResource<EmcReportTmplService, EmcReportTmplBean, Long>
                                               ,GenericActivatableResource<EmcReportTmplService, EmcReportTmplBean, Long>
                                               ,GenericChoosableResource<EmcReportTmplService, EmcReportTmplBean, Long> {

    void copyTo(String id, RestJsonWrapperBean wrapper);
    
    void deleteLine(RestJsonWrapperBean wrapper);
    
    void copyLine(RestJsonWrapperBean wrapper);
    
    Long insertLine(Long reportTmplId, RestJsonWrapperBean wrapper);
    
    Page<EmcReportTmplLineBean> selectLinePagation(Long reportTmplId, RestJsonWrapperBean wrapper);

}
