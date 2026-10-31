package com.sunwayworld.emc.module.mdm.bizmdm.test.service;

import java.util.List;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-09-19
 */

public interface EmcItemService
        extends GenericService<EmcItemBean, Long>, GenericActivatableService<EmcItemBean, Long>, GenericChoosableService<EmcItemBean, Long>, GenericSelectableService<EmcItemBean, Long> {
    Long insert(Long testMethodId, RestJsonWrapperBean wrapper);

    void copy(RestJsonWrapperBean jsonWrapper);

    void copyTo(RestJsonWrapperBean jsonWrapper);

    RestValidationResultBean validateItemNameUnique(RestJsonWrapperBean wrapper, Long testMethodId);

    Page<EmcItemBean> selectTestItem(Long correlationId, RestJsonWrapperBean wrapper);

    void insertFormulaRequired(EmcItemBean item);

    void bacthInsertFormulaRequired(List<EmcItemBean> itemList);

}
