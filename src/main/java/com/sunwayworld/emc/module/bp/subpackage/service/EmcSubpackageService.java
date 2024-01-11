package com.sunwayworld.emc.module.bp.subpackage.service;

import com.sunwayworld.emc.module.bp.subpackage.bean.EmcSubpackageBean;
import com.sunwayworld.emc.module.bp.subpackage.bean.EmcSubpackageTaskBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
* @author zhengxy@sunwayworld.com 2022年09月05日
*/
public interface EmcSubpackageService extends GenericService<EmcSubpackageBean, Long> {

    void createPackage(RestJsonWrapperBean wrapper);

    void addToPackage(RestJsonWrapperBean wrapper);

    void assignPackage(RestJsonWrapperBean wrapper);

    void createContract(RestJsonWrapperBean wrapper);

    Page<EmcSubpackageTaskBean> queryTasks(RestJsonWrapperBean wrapper);

    void createProjPackage(RestJsonWrapperBean wrapper);

    String cancelPackage(RestJsonWrapperBean wrapper);

    void deletePackageTask(RestJsonWrapperBean wrapper);

    void completePackage(RestJsonWrapperBean wrapper);

    void submitToSubpackage(RestJsonWrapperBean wrapper);

    void undoSubpackage(RestJsonWrapperBean wrapper);

}
