package com.sunwayworld.owned.equipment;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/secure/emc/module/mdm/equipt/equipt-maintain-repairs")
public final class OwnedEquipmentMaintainRepairWriteController {
    private final OwnedEquipmentMaintainRepairDao dao;
    public OwnedEquipmentMaintainRepairWriteController(OwnedEquipmentMaintainRepairDao dao) { this.dao = dao; }
    @RequestMapping(method=RequestMethod.POST) public Long insert(RestJsonWrapperBean wrapper) { return dao.insert(wrapper); }
    @RequestMapping(method=RequestMethod.DELETE) public void delete(RestJsonWrapperBean wrapper) { dao.delete(wrapper); }
}
