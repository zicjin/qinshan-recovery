package com.sunwayworld.emc.module.bp.info.reporttmpl.service;

import com.sunwayworld.emc.module.bp.info.reporttmpl.bean.EmcReportTmplBean;
import com.sunwayworld.emc.module.bp.info.reporttmpl.bean.EmcReportTmplLineBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

public interface EmcReportTmplService extends GenericService<EmcReportTmplBean, Long>, GenericActivatableService<EmcReportTmplBean, Long>, GenericChoosableService<EmcReportTmplBean, Long> {

    void copyTo(String id, RestJsonWrapperBean wrapper);
    
    void copyLine(RestJsonWrapperBean wrapper);
    
    void deleteLine(RestJsonWrapperBean wrapper);
    
    Long insertLine(Long reportTmplId, RestJsonWrapperBean wrapper);
    
    Page<EmcReportTmplLineBean> selectLinePagation(Long reportTmplId, RestJsonWrapperBean wrapper);
    

}
