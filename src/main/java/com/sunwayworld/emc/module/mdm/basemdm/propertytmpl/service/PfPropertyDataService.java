package com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service;

import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author zhengxy@sunwayworld.com 2021年06月16日
 */
public interface PfPropertyDataService extends EmcPropertyDataService {

    EmcPropertyDataBean selectPropertyDataByFolderId(Long folderId);
    
    EmcPropertyDataBean selectPropertyDataByOrderTaskId(Long tmplId, Long orderTaskId);
    
    void savePropertyDataByOrderTaskId(Long tmplId,Long orderTaskId, RestJsonWrapperBean wrapper);
    
    void batchUpdatePropertyDataByOrderTaskId(Long tmplId,Long flag1,RestJsonWrapperBean wrapper);

    Long batchUpdatePropertyDataByTestMethodId(Long tmplId, RestJsonWrapperBean wrapper);
    
    EmcPropertyDataBean selectPropertyDataByTestCapacityId(Long tmplId, Long testCapacityId,Long flag1);

    Long savePropertyDataByTestCapacityIdtmplId(Long tmplId, Long testCapacityId,Long flag1, RestJsonWrapperBean wrapper);

    void copyTestMethodParam(RestJsonWrapperBean wrapper);

    Long batchUpdatePropertyDataByTestCapacityId(Long tmplId,Long flag1, RestJsonWrapperBean wrapper);
    
    void batchUpdatePropertyDataByOrder(Long tmplId,Long flag1,RestJsonWrapperBean wrapper);

}
