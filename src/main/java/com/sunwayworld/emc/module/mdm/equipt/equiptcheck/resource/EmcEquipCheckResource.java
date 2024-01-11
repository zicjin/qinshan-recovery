package com.sunwayworld.emc.module.mdm.equipt.equiptcheck.resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.bean.EmcEquipCheckBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.bean.EmcEquipDataBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.bean.EmcEquipPartBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.service.EmcEquipCheckService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/mdm/equipt/equip-checks")
public interface EmcEquipCheckResource extends GenericResource<EmcEquipCheckService, EmcEquipCheckBean, Long>, GenericAuditableResource<EmcEquipCheckService, EmcEquipCheckBean, Long> {

    @RequestMapping(value = "/{id}/equiptParts/queries", method = RequestMethod.POST)
    Page<EmcEquipPartBean> selectEquiptPartPaginationById(@PathVariable Long id, RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/{id}/equiptParts", method = RequestMethod.POST)
    Long insertEquiptPart(@PathVariable Long id, RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/equiptParts", method = RequestMethod.DELETE)
    void deleteEquiptPart(RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/{id}/equiptDatas/queries", method = RequestMethod.POST)
    Page<EmcEquipDataBean> selectEquiptDataPaginationById(@PathVariable Long id, RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/{id}/equiptDatas", method = RequestMethod.POST)
    Long insertEquiptData(@PathVariable Long id, RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/equiptDatas", method = RequestMethod.DELETE)
    void deleteEquiptData(RestJsonWrapperBean wrapper);
}
