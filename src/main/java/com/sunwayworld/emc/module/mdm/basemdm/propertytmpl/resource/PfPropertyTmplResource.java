package com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.resource;

import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyFieldBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author zhengxy@sunwayworld.com 2021年06月15日
 */
public interface PfPropertyTmplResource extends EmcPropertyTmplResource {

    Page<EmcPropertyFieldBean> selectPropertyFieldByMonitorTmplId(String monitorTmplId);

    EmcPropertyDataBean selectPropertyDataByFolderId(Long folderId);

    EmcPropertyDataBean selectPropertyDataByOrderTaskId(Long tmplId, Long orderTaskId);

    void savePropertyDataByOrderTaskId(Long tmplId, Long orderTaskId,RestJsonWrapperBean wrapper);

    void batchUpdatePropertyDataByOrderTaskId(Long tmplId,Long flag1,RestJsonWrapperBean wrapper);

    Long batchUpdatePropertyDataByTestMethodId(Long tmplId,RestJsonWrapperBean wrapper);

    void savePropertyDataByTestCapacityId(Long tmplId, Long testCapacityId,Long flag1, RestJsonWrapperBean wrapper);

    EmcPropertyDataBean selectPropertyDataByTestCapacityId(Long tmplId, Long testCapacityId,Long flag1);

    Page<EmcPropertyFieldBean> selectTestCapacityByTmplId(Long id);

    void copyTestMethodParam(RestJsonWrapperBean wrapper);

    Long batchUpdatePropertyDataByTestCapacityId(Long tmplId,Long flag1,RestJsonWrapperBean wrapper);

    void batchUpdatePropertyDataByOrder(Long tmplId,Long flag1,RestJsonWrapperBean wrapper);

    Page<EmcPropertyFieldBean> selectPropertyFieldByRunId(String runId);

    EmcPropertyDataBean selectPropertyDataByRunId(String runId);

    void saveResultRunPropertyDataByRunId(String runId, RestJsonWrapperBean wrapper);
}
