package com.sunwayworld.owned.equipment;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.PfEquiptMaintainRepairDTO;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/** Source-owned facade for the original maintenance/repair summary page. */
@RestController
@RequestMapping("/secure/emc/module/mdm/equipt/equipts")
public final class OwnedEquipmentMaintenanceSummaryController {
    private final OwnedEquipmentMaintainRepairDao dao;
    public OwnedEquipmentMaintenanceSummaryController(OwnedEquipmentMaintainRepairDao dao) { this.dao = dao; }

    @RequestMapping(value = "/action/select-equipt-maintain-repair-search", method = RequestMethod.POST)
    public Page<PfEquiptMaintainRepairDTO> read(RestJsonWrapperBean wrapper) {
        return dao.readReport(wrapper);
    }
}
