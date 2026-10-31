package com.sunwayworld.owned.equipment;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptMaintainRepairBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.EmcEquiptMaintainRepairDao;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptMaintainRepairService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/** Narrow read service for the original maintenance/repair resource. */
public final class OwnedEquipmentMaintainRepairService implements EmcEquiptMaintainRepairService {
    private final OwnedEquipmentMaintainRepairDao dao;
    public OwnedEquipmentMaintainRepairService(OwnedEquipmentMaintainRepairDao dao) { this.dao = dao; }
    @Override public EmcEquiptMaintainRepairDao getDao() { return dao; }
    @Override public Page<EmcEquiptMaintainRepairBean> selectPaginationByFilter(SearchFilter filter, RestJsonWrapperBean wrapper) {
        Object id = filter == null ? null : filter.getFilterMap().get("equiptId");
        if (id == null && filter != null && !filter.getStdFilterList().isEmpty()) id = filter.getStdFilterList().get(0).getMap().values().iterator().next();
        if (id == null) throw new IllegalArgumentException("equipment id is required");
        return dao.read(Long.valueOf(String.valueOf(id)), wrapper);
    }
}
