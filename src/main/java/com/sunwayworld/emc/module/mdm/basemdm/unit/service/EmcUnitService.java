package com.sunwayworld.emc.module.mdm.basemdm.unit.service;

import org.springframework.web.bind.annotation.PathVariable;

import com.sunwayworld.emc.module.mdm.basemdm.unit.bean.EmcUnitBean;
import com.sunwayworld.emc.module.mdm.basemdm.unit.bean.EmcUnitConvertBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;

/**
 * @author yuanhh@sunwayworld.com 2019年9月9日
 */

public interface EmcUnitService extends GenericService<EmcUnitBean, String>, GenericChoosableService<EmcUnitBean, String>, GenericSelectableService<EmcUnitBean, String> {
    String insert(RestJsonWrapperBean wrapper);

    Page<EmcUnitConvertBean> selectConvertPaginationByUnit(@PathVariable String id, RestJsonWrapperBean wrapper);

    void insertUnitConvert(RestJsonWrapperBean wrapper);

    void deleteUnitConvert(RestJsonWrapperBean wrapper);
}
