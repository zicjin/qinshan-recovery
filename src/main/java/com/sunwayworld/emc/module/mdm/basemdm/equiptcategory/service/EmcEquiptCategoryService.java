package com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.service;

import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.bean.EmcEquiptCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.bean.EmcEquiptMaintBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;

public interface EmcEquiptCategoryService extends GenericService<EmcEquiptCategoryBean, Long>, GenericSelectableService<EmcEquiptCategoryBean, Long> {
    Page<EmcEquiptMaintBean> selectEquiptMaintPaginationByEquiptCategoryId(Long id, RestJsonWrapperBean wrapper);

    Long insertEquiptMaint(Long id, RestJsonWrapperBean wrapper);

    void deleteEquiptMaint(Long id, RestJsonWrapperBean wrapper);
}
