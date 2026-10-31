package com.sunwayworld.owned.equipment;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.*;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.EmcEquiptDao;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;

/** Bounded equipment implementation outside the unchanged business package. */
public final class OwnedEquipmentReadService implements EmcEquiptService {
    private final OwnedEquipmentReadDao dao;
    public OwnedEquipmentReadService(OwnedEquipmentReadDao dao) { this.dao=dao; }
    @Override public EmcEquiptDao getDao() { return dao; }
    @Override public Page<EmcEquiptBean> selectPagination(RestJsonWrapperBean wrapper) { return dao.readPage(wrapper); }
    @Override public Page<EmcEquiptBean> selectRawPagination(RestJsonWrapperBean wrapper) { return dao.readPage(wrapper); }
    @Override public EmcEquiptBean selectDetail(Long id) { return dao.readDetail(id); }
    @Override public Long insert(RestJsonWrapperBean wrapper) { return dao.insertEquipment(wrapper); }
    @Override public void save(Long id, RestJsonWrapperBean wrapper) { dao.updateEquipment(id, wrapper); }
    @Override public void delete(RestJsonWrapperBean wrapper) { dao.deleteEquipment(wrapper); }
    @Override public Long insertTestMethod(Long id, RestJsonWrapperBean wrapper) { return dao.insertTestMethod(id, wrapper); }
    @Override public Page<EmcEquiptTestMethodBean> selectEquiptTestMethodRawPagination(Long id, RestJsonWrapperBean wrapper) { return dao.readTestMethods(id, wrapper); }
    @Override public void deleteTestMethod(Long id, RestJsonWrapperBean wrapper) { dao.deleteTestMethod(id, wrapper); }
    @Override public EmcEquiptDCUConfBean selectDCUConfDetail(Long id) { return dao.readDcuConfig(id); }
    @Override public Page<EmcEquiptTestItemBean> selectEquiptTestItemRawPagination(Long id, RestJsonWrapperBean wrapper) { return dao.readTestItems(id, wrapper); }
    @Override public Long insertTestItem(Long id, RestJsonWrapperBean wrapper) { return dao.insertTestItem(id, wrapper); }
    @Override public void deleteTestItem(Long id, RestJsonWrapperBean wrapper) { dao.deleteTestItem(id, wrapper); }
    @Override public Page<EmcEquiptBean> selectChoosablePagination(RestJsonWrapperBean wrapper) { return dao.readPage(wrapper); }
    @Override public RestValidationResultBean validateDcuConfig(Long id) { return dao.validateDcuConfig(id); }
    @Override public Page<EmcEquiptBean> selectCaliChoose(RestJsonWrapperBean wrapper) { return dao.readChoice(wrapper, "equiptCalibration", false); }
    @Override public Page<EmcEquiptBean> selectVerifyChoose(RestJsonWrapperBean wrapper) { return dao.readChoice(wrapper, "equiptVerification", false); }
    @Override public Page<EmcEquiptBean> selectPerCheckChoose(RestJsonWrapperBean wrapper) { return dao.readChoice(wrapper, "duringCheck", true); }
    private static UnsupportedOperationException unsupported() {
        return new UnsupportedOperationException("Equipment write or auxiliary flow is not implemented");
    }
}
