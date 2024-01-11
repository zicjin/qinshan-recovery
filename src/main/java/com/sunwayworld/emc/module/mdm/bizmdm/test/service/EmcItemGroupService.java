package com.sunwayworld.emc.module.mdm.bizmdm.test.service;

import java.util.List;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemGroupBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-09-19
 */

public interface EmcItemGroupService extends GenericService<EmcItemGroupBean, Long>, GenericActivatableService<EmcItemGroupBean, Long>, GenericChoosableService<EmcItemGroupBean, Long> {
    Long insert(Long testMethodId, RestJsonWrapperBean wrapper);

    void copy(Long id, RestJsonWrapperBean wrapper);

    List<Long> checkUsed(RestJsonWrapperBean wrapper);

    Long copyOrganicItemGroup(RestJsonWrapperBean wrapper);

    void editOrganicItemGroup(RestJsonWrapperBean wrapper);

    void copyOrganicFormulaTo(Long itemGroupId, RestJsonWrapperBean wrapper);
}
