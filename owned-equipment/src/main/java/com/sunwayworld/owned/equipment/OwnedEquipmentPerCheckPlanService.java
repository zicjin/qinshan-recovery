package com.sunwayworld.owned.equipment;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPerCheckPlanBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.EmcEquiptPerCheckPlanDao;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptPerCheckPlanService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;

/** Isolated period-check plan service for the unchanged equipment resource. */
public final class OwnedEquipmentPerCheckPlanService implements EmcEquiptPerCheckPlanService {
    private final OwnedEquipmentPerCheckPlanDao dao;
    public OwnedEquipmentPerCheckPlanService(OwnedEquipmentPerCheckPlanDao dao) { this.dao = dao; }
    @Override public EmcEquiptPerCheckPlanDao getDao() { return dao; }
    @Override public Long insert(RestJsonWrapperBean wrapper) { return dao.insertPlan(wrapper); }
    @Override public void delete(RestJsonWrapperBean wrapper) { dao.deletePlans(wrapper); }
    @Override public Page<EmcEquiptPerCheckPlanBean> selectPaginationByFilter(SearchFilter filter, RestJsonWrapperBean wrapper) {
        Object id = filter == null ? null : filter.getFilterMap().get("equiptId");
        if (id == null && filter != null && !filter.getStdFilterList().isEmpty()) id = filter.getStdFilterList().get(0).getMap().values().iterator().next();
        if (id == null) throw new IllegalArgumentException("equipment id is required");
        return dao.readPlans(Long.valueOf(String.valueOf(id)), wrapper);
    }
    @Override public void updatePlan(EmcEquiptPerCheckPlanBean value) { dao.updatePlan(value); }
}
