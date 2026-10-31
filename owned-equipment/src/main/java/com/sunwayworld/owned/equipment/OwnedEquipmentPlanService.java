package com.sunwayworld.owned.equipment;

import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.bean.EmcEquiptMaintBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPlanBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.EmcEquiptPlanDao;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptPlanService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import java.util.List;

/** Narrow maintenance-plan service bound to the owned isolated Oracle adapter. */
public final class OwnedEquipmentPlanService implements EmcEquiptPlanService {
    private final OwnedEquipmentPlanDao dao;
    public OwnedEquipmentPlanService(OwnedEquipmentPlanDao dao) { this.dao = dao; }
    @Override public EmcEquiptPlanDao getDao() { return dao; }
    @Override public Long insert(RestJsonWrapperBean wrapper) { return dao.insertPlan(wrapper); }
    @Override public void delete(RestJsonWrapperBean wrapper) { dao.deletePlans(wrapper); }
    @Override public Page<EmcEquiptPlanBean> selectRawPaginationByEquiptId(Long id, RestJsonWrapperBean wrapper) { return dao.readPlans(id, wrapper); }
    @Override public List<EmcEquiptMaintBean> selectMaintSelectableList(Long id, RestJsonWrapperBean wrapper) { return dao.selectableMaintenance(id, wrapper); }
    @Override public Page<EmcEquiptPlanBean> selectEquiptMaintPlanOverdue(RestJsonWrapperBean wrapper) { return dao.readOverdue(wrapper); }
    @Override public void updatePlan(EmcEquiptPlanBean bean) { dao.updatePlan(bean); }
}
