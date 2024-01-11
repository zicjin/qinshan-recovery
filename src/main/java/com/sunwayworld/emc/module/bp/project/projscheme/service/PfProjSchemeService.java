package com.sunwayworld.emc.module.bp.project.projscheme.service;

import java.util.List;

import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjOrderBean;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjSchemeBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author zhengxy@sunwayworld.com 2021年06月01日
 */
public interface PfProjSchemeService extends EmcProjSchemeService {

    List<String> generateFolderNos(EmcProjectBean project, String orderCategoryCode, int num);

    void insertBpDataByScheme(List<EmcProjSchemeBean> schemeList);

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

    void syncNewProjScheme(List<EmcProjSchemeBean> schemeList);

    void syncNewProjOrder(List<EmcProjSchemeBean> schemeList);

    void deleteSyncProjScheme(List<EmcProjSchemeBean> schemeList);

    void deleteSyncProjOrder(List<EmcProjSchemeBean> schemeList, List<EmcProjOrderBean> projOrderList);

    void move(RestJsonWrapperBean wrapper);

    void moveProjOrder(RestJsonWrapperBean wrapper);
}
