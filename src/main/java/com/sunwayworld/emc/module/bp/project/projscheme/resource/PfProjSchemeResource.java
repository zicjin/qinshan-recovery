package com.sunwayworld.emc.module.bp.project.projscheme.resource;

import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjOrderBean;
import com.sunwayworld.framework.data.page.Page;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@RequestMapping("/secure/emc/module/bp/project/proj-schemes")
public interface PfProjSchemeResource extends EmcProjSchemeResource {

    void syncFolder(RestJsonWrapperBean wrapper);

    void updateScheme(RestJsonWrapperBean wrapper);

    void updateSchemeOrder(RestJsonWrapperBean wrapper);

    void syncNewFolder(RestJsonWrapperBean wrapper);

    void syncNewTest(RestJsonWrapperBean wrapper);

    void deleteFolder(RestJsonWrapperBean wrapper);

    void syncFolderFromScheme(RestJsonWrapperBean wrapper);

    void deleteOrder(RestJsonWrapperBean wrapper);

    void syncOrderFromScheme(RestJsonWrapperBean wrapper);

    void copyOrder(RestJsonWrapperBean wrapper);

    void deleteTest(RestJsonWrapperBean wrapper);

    void syncTestFromScheme(RestJsonWrapperBean wrapper);

    void completeFolder(RestJsonWrapperBean wrapper);

    void completeFolderForOrder(RestJsonWrapperBean wrapper);

    void cancelFolder(RestJsonWrapperBean wrapper);

    void cancelFolderForOrder(RestJsonWrapperBean wrapper);

    String removeOrderTask(RestJsonWrapperBean wrapper);

    Page<EmcProjOrderBean> selectProjOrderProjectPagationByProjScheme(Long id, RestJsonWrapperBean wrapper);

    Page<EmcProjOrderBean> selectProjOrderOrderPagationByProjScheme(Long id, RestJsonWrapperBean wrapper);

    void move(RestJsonWrapperBean wrapper);

    void moveProjOrder(RestJsonWrapperBean wrapper);

}
