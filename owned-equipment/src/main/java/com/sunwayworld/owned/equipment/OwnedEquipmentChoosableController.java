package com.sunwayworld.owned.equipment;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/** Source-owned route facade for the generic equipment choice page. */
@RestController
@RequestMapping("/secure/emc/module/mdm/equipt/equipts")
public final class OwnedEquipmentChoosableController {
    private final OwnedEquipmentReadDao dao;

    public OwnedEquipmentChoosableController(OwnedEquipmentReadDao dao) { this.dao = dao; }

    @RequestMapping(value = "/queries/choosable", method = RequestMethod.POST)
    public Page<EmcEquiptBean> read(RestJsonWrapperBean wrapper) { return dao.readPage(wrapper); }
}
