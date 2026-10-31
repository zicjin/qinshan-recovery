package com.sunwayworld.emc.module.mdm.basemdm.unit.resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sunwayworld.emc.module.mdm.basemdm.unit.bean.EmcUnitBean;
import com.sunwayworld.emc.module.mdm.basemdm.unit.bean.EmcUnitConvertBean;
import com.sunwayworld.emc.module.mdm.basemdm.unit.service.EmcUnitService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableResource;

@RequestMapping("/secure/emc/module/mdm/basemdm/units")
public interface EmcUnitResource extends GenericResource<EmcUnitService, EmcUnitBean, String>, GenericChoosableResource<EmcUnitService, EmcUnitBean, String>, GenericSelectableResource<EmcUnitService, EmcUnitBean, String> {

    @RequestMapping(value = "/{id}/converts/queries", method = RequestMethod.POST)
    Page<EmcUnitConvertBean> selectConvertPaginationByUnit(@PathVariable String id, RestJsonWrapperBean wrapper);

    void insertUnitConvert(RestJsonWrapperBean wrapper);

    void deleteUnitConvert(RestJsonWrapperBean wrapper);
}
