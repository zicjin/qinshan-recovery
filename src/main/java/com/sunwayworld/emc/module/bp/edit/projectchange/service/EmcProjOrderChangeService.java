package com.sunwayworld.emc.module.bp.edit.projectchange.service;

import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcProjOrderChangeBean;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjDTO;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-10-08
 */

public interface EmcProjOrderChangeService extends GenericService<EmcProjOrderChangeBean, Long> {

    Page<EmcProjDTO> selectByProjId(Long projId);

    void editSubcontractor(RestJsonWrapperBean wrapper);

    void updateOrderIndex(RestJsonWrapperBean wrapper);

    void relevanceUpdateSubcontractor(RestJsonWrapperBean wrapper);

    void relevanceDeleteSubcontractor(RestJsonWrapperBean wrapper);
}
