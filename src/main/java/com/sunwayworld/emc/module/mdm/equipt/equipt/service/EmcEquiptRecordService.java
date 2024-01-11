package com.sunwayworld.emc.module.mdm.equipt.equipt.service;

import java.util.List;

import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.bean.EmcEquiptMaintBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptRecordBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-19
 */

public interface EmcEquiptRecordService extends GenericService<EmcEquiptRecordBean, Long> {


    void complete(RestJsonWrapperBean wrapper);

    List<EmcEquiptMaintBean> selectRecordMaintSelectableList(Long id);

    Page<EmcEquiptRecordBean> selectRawPaginationByEquiptId(Long equiptId, RestJsonWrapperBean wrapper);
}
