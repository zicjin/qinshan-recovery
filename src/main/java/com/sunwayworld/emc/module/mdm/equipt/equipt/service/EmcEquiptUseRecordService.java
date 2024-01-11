package com.sunwayworld.emc.module.mdm.equipt.equipt.service;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptUseRecordBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcEquiptUseRecordService extends GenericService<EmcEquiptUseRecordBean, Long> {

    void updateUseRecord(RestJsonWrapperBean wrapper);

    Page<EmcEquiptUseRecordBean> selectRawPaginationByEquiptIdOrSampleId(Long id, RestJsonWrapperBean wrapper);

    Page<EmcEquiptUseRecordBean> selectEquiptUseRecordPagination(RestJsonWrapperBean wrapper);
}
