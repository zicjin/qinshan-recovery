package com.sunwayworld.emc.module.bp.bp.service;

import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;

/**
 * @author zhengxy@sunwayworld.com 2021年07月19日
 */
public interface PfFolderService extends EmcFolderService {

    String generateFolderNo(EmcProjectBean project, String monitorTmplId);
    
    String submitRemind (RestJsonWrapperBean wrapper);

    void submitProject(RestJsonWrapperBean wrapper);

    void updateByProject(RestJsonWrapperBean wrapper);

    RestValidationResultBean customizeValidate(Long id, String columnName, String columnValue);

    void sortFolderNo(RestJsonWrapperBean wrapper);

}
