package com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service;

import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcPropertyDataService extends GenericService<EmcPropertyDataBean, Long> {

    EmcPropertyDataBean selectPropertyData(Long id, Long tmplId);

    EmcPropertyDataBean selectPropertyData(Long id, Long monitorPointId, String monitorPointCategoryId);

    EmcPropertyDataBean selectPropertyDataByMonitorPointId(Long id);

    EmcPropertyDataBean selectPropertyDataByTestMethodId(Long tmplId, Long testMethodId);

    EmcPropertyDataBean selectPropertyDataByRunId(Long tmplId, String runId);

    Long savePropertyDataByTestMethodId(Long tmplId, Long testMethodId, RestJsonWrapperBean wrapper);

    String saveResultRunPropertyDataByRunId(Long tmplId, String runId, RestJsonWrapperBean wrapper);

    void insertIndexSetsPropertyData(RestJsonWrapperBean wrapper);

    void batchUpdatePropertyData(RestJsonWrapperBean wrapper);

    void updatePropertyDataByMonitorPoint(RestJsonWrapperBean wrapper);
}
