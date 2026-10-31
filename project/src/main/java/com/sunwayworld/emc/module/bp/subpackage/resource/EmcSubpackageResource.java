package com.sunwayworld.emc.module.bp.subpackage.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.subpackage.bean.EmcSubpackageBean;
import com.sunwayworld.emc.module.bp.subpackage.bean.EmcSubpackageTaskBean;
import com.sunwayworld.emc.module.bp.subpackage.service.EmcSubpackageService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
* @author zhengxy@sunwayworld.com 2022年09月05日
*/
@RequestMapping("/secure/emc/module/bp/subpackages")
public interface EmcSubpackageResource extends GenericResource<EmcSubpackageService, EmcSubpackageBean, Long> {

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
