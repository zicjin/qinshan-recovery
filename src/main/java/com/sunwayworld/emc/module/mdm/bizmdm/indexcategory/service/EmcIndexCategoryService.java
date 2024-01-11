package com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.service;

import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcIndexCategoryBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;

public interface EmcIndexCategoryService extends GenericService<EmcIndexCategoryBean, Long>, GenericChoosableService<EmcIndexCategoryBean, Long>, GenericActivatableService<EmcIndexCategoryBean, Long>,
        GenericSelectableService<EmcIndexCategoryBean, Long> {

    Long insert(RestJsonWrapperBean wrapper);

    void sync(RestJsonWrapperBean wrapper);

    String estandSync();

    String estandUpdate();

    void addIndex(RestJsonWrapperBean wrapper);

    Page<EmcIndexCategoryBean> selectByProjScheme(RestJsonWrapperBean wrapper);

    void estandSync(RestJsonWrapperBean wrapper);

    Page<EmcIndexCategoryBean> selectByOrder(RestJsonWrapperBean wrapper);
}
