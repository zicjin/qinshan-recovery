package com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.service;

import java.util.List;

import com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.bean.EmcFieldSetBean;
import com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.bean.EmcStandardSetBean;
import com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.bean.EmcStdSetCategoryBean;
import com.sunwayworld.framework.beans.BeanPropertyDescriptor;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcStdSetCategoryService extends GenericService<EmcStdSetCategoryBean, Long> {
    Long insert(RestJsonWrapperBean wrapper);

    Long insertStandardSet(Long id, RestJsonWrapperBean wrapper);

    Page<EmcStandardSetBean> selectStandardSetPaginationById(Long id, RestJsonWrapperBean wrapper);

    void deleteStandardSet(RestJsonWrapperBean wrapper);

    Long insertFieldSet(Long standardSetId, RestJsonWrapperBean wrapper);

    Page<EmcFieldSetBean> selectFieldSetPaginationByStandardSetId(Long standardSetId, RestJsonWrapperBean wrapper);

    void deleteFieldSet(RestJsonWrapperBean wrapper);

    Page<BeanPropertyDescriptor> selectFieldInfoPagination(RestJsonWrapperBean wrapper);

    List<EmcStandardSetBean> selectStandardSetCategorySelectableList();

    EmcStandardSetBean selectStandardSetDetail(Long standSetId);
    
    List<EmcFieldSetBean> selectFieldSetList(RestJsonWrapperBean wrapper);
}
