package com.sunwayworld.owned.equipment;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptUseRecordBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.EmcEquiptUseRecordDao;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptUseRecordService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/** Isolated equipment-use-record service bound to the owned Oracle adapter. */
public final class OwnedEquipmentUseRecordService implements EmcEquiptUseRecordService {
    private final OwnedEquipmentUseRecordDao dao;
    public OwnedEquipmentUseRecordService(OwnedEquipmentUseRecordDao dao) { this.dao = dao; }
    @Override public EmcEquiptUseRecordDao getDao() { return dao; }
    @Override public Long insert(RestJsonWrapperBean wrapper) { return dao.insertRecords(wrapper); }
    @Override public void updateUseRecord(RestJsonWrapperBean wrapper) { dao.updateRecords(wrapper); }
    @Override public void delete(RestJsonWrapperBean wrapper) { dao.deleteRecords(wrapper); }
    @Override public Page<EmcEquiptUseRecordBean> selectRawPaginationByEquiptIdOrSampleId(Long id, RestJsonWrapperBean wrapper) {
        return dao.readByEquipmentOrSample(id, wrapper);
    }
    @Override public Page<EmcEquiptUseRecordBean> selectEquiptUseRecordPagination(RestJsonWrapperBean wrapper) {
        return dao.readAll(wrapper);
    }
}
