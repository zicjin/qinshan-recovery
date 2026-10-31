package com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service;



import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyFieldBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author licm@sunwayworld.com 2021年01月12日
 */
public interface PfPropertyTmplService extends EmcPropertyTmplService {

    EmcPropertyDataBean selectPropertyDataByOrderTaskId(Long tmplId, Long orderTaskId);


    void savePropertyDataByOrderTaskId(Long tmplId,Long orderTaskId,RestJsonWrapperBean wrapper);

    void batchUpdatePropertyDataByOrderTaskId(Long tmplId,Long flag1,RestJsonWrapperBean wrapper);

    Long batchUpdatePropertyDataByTestMethodId(Long tmplId,RestJsonWrapperBean wrapper);

    Long savePropertyDataByTestCapacityId(Long tmplId, Long testCapacityId,Long flag1, RestJsonWrapperBean wrapper);

    EmcPropertyDataBean selectPropertyDataByTestCapacityId(Long tmplId, Long testCapacityId,Long flag1);

    void copyTestMethodParam(RestJsonWrapperBean wrapper);

    Long batchUpdatePropertyDataByTestCapacityId(Long tmplId,Long flag1,RestJsonWrapperBean wrapper);

    void batchUpdatePropertyDataByOrder(Long tmplId,Long flag1,RestJsonWrapperBean wrapper);

    Page<EmcPropertyFieldBean> selectPropertyFieldByRunId(String runId);

    EmcPropertyDataBean selectPropertyDataByRunId(String runId);

    void saveResultRunPropertyDataByRunId(String runId, RestJsonWrapperBean wrapper);
}
