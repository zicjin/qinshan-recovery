package com.sunwayworld.owned.consumables;

import com.sunwayworld.emc.module.item.consume.consume.bean.EmcConsumeBean;
import com.sunwayworld.emc.module.item.consume.consume.dao.EmcConsumeDao;
import com.sunwayworld.emc.module.item.consume.consume.service.EmcConsumeService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/** Original EmcConsumeService contract with bounded reads and isolated material creation. */
public final class OwnedConsumablesReadService implements EmcConsumeService {
    private final OwnedConsumablesReadDao dao;
    public OwnedConsumablesReadService(OwnedConsumablesReadDao dao) { this.dao = dao; }
    @Override public EmcConsumeDao getDao() { return dao; }
    @Override public Page<EmcConsumeBean> selectPagination(RestJsonWrapperBean wrapper) { return dao.readPage(wrapper); }
    @Override public Page<EmcConsumeBean> selectRawPagination(RestJsonWrapperBean wrapper) { return dao.readPage(wrapper); }
    @Override public EmcConsumeBean selectDetail(String id) { return dao.readDetail(id); }
    @Override public String insert(RestJsonWrapperBean wrapper) { return dao.insertMaterial(wrapper); }
    @Override public void save(String id, RestJsonWrapperBean wrapper) { dao.updateMaterial(id, wrapper); }
    @Override public void delete(RestJsonWrapperBean wrapper) { dao.deleteMaterials(wrapper); }
    @Override public void updateStockQty(String id) { dao.updateStockQty(id); }
}
