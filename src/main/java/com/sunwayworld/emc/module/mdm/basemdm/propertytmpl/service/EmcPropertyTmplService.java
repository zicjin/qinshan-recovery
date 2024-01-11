package com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service;

import java.util.List;

import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyFieldBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyTmplBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;

public interface EmcPropertyTmplService extends GenericService<EmcPropertyTmplBean, Long>, GenericChoosableService<EmcPropertyTmplBean, Long>, GenericSelectableService<EmcPropertyTmplBean, Long> {

    Long insert(RestJsonWrapperBean wrapper);

    Long insertField(Long id, RestJsonWrapperBean wrapper);

    Page<EmcPropertyFieldBean> selectFieldPaginationById(Long id, RestJsonWrapperBean wrapper);

    EmcPropertyFieldBean selectFieldById(Long itemId);

    void deleteField(RestJsonWrapperBean wrapper);

    Page<EmcPropertyFieldBean> selectTmpls(Long id, String monitorTmplId);

    Page<EmcPropertyFieldBean> selectTmpls(Long id, Long monitorPointId, String monitorPointCategoryId);

    EmcPropertyDataBean selectPropertyData(Long id, Long tmplId);

    EmcPropertyDataBean selectPropertyData(Long id, Long monitorPointId, String monitorPointCategoryId);

    EmcPropertyDataBean selectPropertyDataByMonitorPointId(Long id);

    Page<EmcPropertyFieldBean> selectPropertyFieldByMonitorPointId(Long id);

    Page<EmcPropertyFieldBean> selectPropertyFieldByTmplId(Long id);

    EmcPropertyDataBean selectPropertyDataByTestMethodId(Long tmplId, Long testMethodId);

    Long savePropertyDataByTestMethodId(Long tmplId, Long testMethodId, RestJsonWrapperBean wrapper);

    EmcPropertyDataBean selectPropertyDataByRunId(Long tmplId, String runId);

    String saveResultRunPropertyDataByRunId(Long tmplId, String runId, RestJsonWrapperBean wrapper);

    void save(Long id, RestJsonWrapperBean wrapper);

    void batchUpdatePropertyData(RestJsonWrapperBean wrapper);

    List<EmcPropertyFieldBean> selectFieldsByMonitorPointCategory(String tmplName);

    void updatePropertyFieldByMonitorPointCategory(RestJsonWrapperBean wrapper);

    void updatePropertyDataByMonitorPoint(RestJsonWrapperBean wrapper);

    void copy(RestJsonWrapperBean wrapper);
}
