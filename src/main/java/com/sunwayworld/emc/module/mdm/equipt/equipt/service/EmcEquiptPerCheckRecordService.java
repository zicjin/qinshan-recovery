package com.sunwayworld.emc.module.mdm.equipt.equipt.service;

import java.util.List;

import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.bean.EmcEquiptMaintBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPerCheckRecordBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author liuyz@sunwayworld.com 2020年09月25日
 */
public interface EmcEquiptPerCheckRecordService extends GenericService<EmcEquiptPerCheckRecordBean,Long> {

    List<EmcEquiptMaintBean> selectRecordMaintSelectableList(Long equiptId);

    void complete(RestJsonWrapperBean wrapper);
}
