package com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.service;

import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcIndexSetBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

import java.util.List;

public interface EmcIndexSetService extends GenericService<EmcIndexSetBean, Long>, GenericChoosableService<EmcIndexSetBean, Long> {

    Long insert(String indexId, Long indexCategoryId, Long tmplId, RestJsonWrapperBean wrapper);

    List<EmcIndexSetBean> selectListByIndexIdAndItemId(Long indexId, Long itemId);
}
