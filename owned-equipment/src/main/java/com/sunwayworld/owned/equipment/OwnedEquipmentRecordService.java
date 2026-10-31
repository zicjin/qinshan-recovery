package com.sunwayworld.owned.equipment;

import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.bean.EmcEquiptMaintBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptRecordBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.EmcEquiptRecordDao;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptRecordService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import java.util.List;

/** Narrow maintenance-record service bound to the owned isolated Oracle adapter. */
public final class OwnedEquipmentRecordService implements EmcEquiptRecordService {
    private final OwnedEquipmentRecordDao dao;
    public OwnedEquipmentRecordService(OwnedEquipmentRecordDao dao) { this.dao = dao; }
    @Override public EmcEquiptRecordDao getDao() { return dao; }
    @Override public Long insert(RestJsonWrapperBean wrapper) { return dao.insertRecord(wrapper); }
    @Override public Page<EmcEquiptRecordBean> selectRawPaginationByEquiptId(Long id, RestJsonWrapperBean wrapper) { return dao.readRecords(id, wrapper); }
    @Override public List<EmcEquiptMaintBean> selectRecordMaintSelectableList(Long id) { return dao.selectableMaintenances(id); }
    @Override public void complete(RestJsonWrapperBean wrapper) { dao.completeRecords(wrapper); }
    @Override public void delete(RestJsonWrapperBean wrapper) { dao.deleteRecords(wrapper); }
}
