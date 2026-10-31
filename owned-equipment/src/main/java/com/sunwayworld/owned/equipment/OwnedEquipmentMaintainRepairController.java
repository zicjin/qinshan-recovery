package com.sunwayworld.owned.equipment;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptMaintainRepairBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptMaintainRepairService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/** Owned route for the historical maintenance/repair read shape. */
@RestController
@RequestMapping("/secure/emc/module/mdm/equipt/equipts")
public final class OwnedEquipmentMaintainRepairController {
    private final EmcEquiptMaintainRepairService service;
    public OwnedEquipmentMaintainRepairController(EmcEquiptMaintainRepairService service) { this.service = service; }
    @RequestMapping(value = "/{id}/maintain/queries", method = RequestMethod.POST)
    public Page<EmcEquiptMaintainRepairBean> read(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return service.selectPaginationByFilter(SearchFilter.instance().match("equiptId", id).filter(MatchPattern.EQ), wrapper);
    }
}
