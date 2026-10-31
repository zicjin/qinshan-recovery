package com.sunwayworld.emc.module.bp.edit.projectchange.service;

import java.util.List;

import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcProjOrderChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcProjSchemeChangeBean;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjDTO;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-10-08
 */

public interface EmcProjSchemeChangeService extends GenericService<EmcProjSchemeChangeBean, Long> {

    void copy(RestJsonWrapperBean wrapper);

    void copyPoint(RestJsonWrapperBean wrapper);

    void updateFrequency(RestJsonWrapperBean wrapper);

    void updateProjOrderItemGroup(RestJsonWrapperBean wrapper);

    Page<EmcProjOrderChangeBean> selectProjOrderPagationByProjScheme(Long id, RestJsonWrapperBean wrapper);

    Page<EmcProjOrderChangeBean> selectOrderPagation(RestJsonWrapperBean wrapper);

    Page<EmcProjDTO> selectByProjId(Long id);

    void insertBpData(List<Long> projectIdList);

    void editSubcontractor(RestJsonWrapperBean wrapper);

    void initInsert(RestJsonWrapperBean wrapper);

    void updateOrderIndex(RestJsonWrapperBean wrapper);

    List<String> generateFolderNosByProjId(Long projId, String projectNo, int num);

    void relevanceUpdateSubcontractor(RestJsonWrapperBean wrapper);

    void relevanceDeleteSubcontractor(RestJsonWrapperBean wrapper);

}
