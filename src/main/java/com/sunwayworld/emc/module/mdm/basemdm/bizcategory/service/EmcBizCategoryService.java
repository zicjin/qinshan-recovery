package com.sunwayworld.emc.module.mdm.basemdm.bizcategory.service;

import java.util.List;

import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.bean.EmcBizCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.bean.EmcBizCategoryProjectBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;

public interface EmcBizCategoryService extends GenericService<EmcBizCategoryBean, Long>, GenericSelectableService<EmcBizCategoryBean, Long>, GenericChoosableService<EmcBizCategoryBean, Long> {

    void insertProject(Long id, RestJsonWrapperBean wrapper);

    Page<EmcBizCategoryProjectBean> selectBizCategoryProjectPaginationByBizCategoryId(Long id, RestJsonWrapperBean wrapper);

    void deleteProject(Long id, RestJsonWrapperBean wrapper);

    Page<EmcBizCategoryProjectBean> selectBizCategoryProjectChoosablePagination(RestJsonWrapperBean wrapper);

    List<EmcBizCategoryProjectBean> selectProjectSelectableList();

}
