package com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.resource;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyFieldBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyTmplBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.EmcPropertyTmplService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableResource;

@RequestMapping("/secure/emc/module/mdm/basemdm/property-tmpls")
public interface EmcPropertyTmplResource extends GenericResource<EmcPropertyTmplService, EmcPropertyTmplBean, Long>, GenericChoosableResource<EmcPropertyTmplService, EmcPropertyTmplBean, Long>,
        GenericSelectableResource<EmcPropertyTmplService, EmcPropertyTmplBean, Long> {

    Long insertPropertyData(RestJsonWrapperBean wrapper);

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

    Page<EmcPropertyFieldBean> selectTestMethodByTmplId(Long id);

    EmcPropertyDataBean selectPropertyDataByTestMethodId(Long tmplId, Long testMethodId);

    void savePropertyDataByTestMethodId(Long tmplId, Long testMethodId, RestJsonWrapperBean wrapper);

    EmcPropertyDataBean selectPropertyDataByRunId(Long tmplId, String runId);

    void saveResultRunPropertyDataByRunId(Long tmplId, String runId, RestJsonWrapperBean wrapper);

    Page<EmcPropertyFieldBean> selectIndexSetByMonitorTmplId(Long id);

    EmcPropertyDataBean selectPropertyDataByIndexId(Long id, Long tmplId);

    void batchUpdatePropertyData(RestJsonWrapperBean wrapper);

    List<EmcPropertyFieldBean> selectFieldsByMonitorPointCategory(String tmplName);

    void updatePropertyFieldByMonitorPointCategory(RestJsonWrapperBean wrapper);

    void updatePropertyDataByMonitorPoint(RestJsonWrapperBean wrapper);

    void copy(RestJsonWrapperBean wrapper);
}
