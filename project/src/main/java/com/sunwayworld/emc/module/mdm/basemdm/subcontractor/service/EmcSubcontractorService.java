package com.sunwayworld.emc.module.mdm.basemdm.subcontractor.service;

import com.sunwayworld.emc.module.mdm.basemdm.subcontractor.bean.EmcSubcontractRecordBean;
import com.sunwayworld.emc.module.mdm.basemdm.subcontractor.bean.EmcSubcontractorBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;

/**
 * @author magh@sunwayworld.com
 * @date 2019-11-05
 */

public interface EmcSubcontractorService extends GenericService<EmcSubcontractorBean, Long>, GenericSelectableService<EmcSubcontractorBean, Long>, GenericChoosableService<EmcSubcontractorBean, Long> {

    Page<EmcSubcontractRecordBean> selectRecordPaginationBySubcontractorId(Long id, RestJsonWrapperBean wrapper);
}
