package com.sunwayworld.emc.module.bp.info.reporttmpl.service;

import com.sunwayworld.emc.module.bp.info.reporttmpl.bean.EmcReportTmplLineBean;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcReportTmplLineService extends GenericService<EmcReportTmplLineBean, Long> {

    Long insert(Long reportTemplId, RestJsonWrapperBean wrapper);

    Long copy(RestJsonWrapperBean wrapper);

    Long selectMaxSorterByCond(SearchFilter filter);

}
