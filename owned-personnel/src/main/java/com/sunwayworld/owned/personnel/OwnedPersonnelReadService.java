package com.sunwayworld.owned.personnel;

import com.sunwayworld.emc.module.mdm.basemdm.cert.bean.EmcCertBean;
import com.sunwayworld.emc.module.mdm.basemdm.cert.bean.EmcCertTestMethodBean;
import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpBean;
import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpQualBean;
import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpTestBean;
import com.sunwayworld.emc.module.mdm.emp.emp.dao.EmcEmpDao;
import com.sunwayworld.emc.module.mdm.emp.emp.service.EmcEmpService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/** Original EmcEmpService contract with a bounded personnel read slice. */
public final class OwnedPersonnelReadService implements EmcEmpService {
    private final OwnedPersonnelReadDao dao;

    public OwnedPersonnelReadService(OwnedPersonnelReadDao dao) {
        this.dao = dao;
    }

    @Override public EmcEmpDao getDao() { return dao; }
    @Override public String insert(RestJsonWrapperBean wrapper) { return dao.insertEmployees(wrapper); }
    @Override public void save(String id, RestJsonWrapperBean wrapper) { dao.updateEmployee(id, wrapper); }
    @Override public void delete(RestJsonWrapperBean wrapper) { dao.deleteEmployees(wrapper); }
    @Override public Page<EmcEmpBean> selectChoosablePagination(RestJsonWrapperBean wrapper) { return dao.readPage(wrapper); }
    @Override public java.util.List<EmcEmpBean> selectSelectableList() { return dao.readSelectableList(); }
    @Override public Long insertQual(String id, RestJsonWrapperBean wrapper) { return dao.insertQualification(id, wrapper); }
    @Override public void insertTest(String id, RestJsonWrapperBean wrapper) { dao.insertTests(id, wrapper); }
    @Override public Page<EmcEmpQualBean> selectQualPagationByEmp(String id, RestJsonWrapperBean wrapper) { return dao.readQualifications(id, wrapper); }
    @Override public Page<EmcEmpTestBean> selectTestPagationByEmp(String id, RestJsonWrapperBean wrapper) { return dao.readTests(id, wrapper); }
    @Override public void deleteQual(String id, RestJsonWrapperBean wrapper) { dao.deleteQualifications(id, wrapper); }
    @Override public void deleteTest(String id, RestJsonWrapperBean wrapper) { dao.deleteTests(id, wrapper); }
    @Override public Page<EmcCertBean> selectCretPagationByEmp(String id, RestJsonWrapperBean wrapper) { return dao.readCredentials(id, wrapper); }
    @Override public Page<EmcCertTestMethodBean> selectCertTestMethodPagationByEmp(Long id, RestJsonWrapperBean wrapper) { return dao.readMethods(id, wrapper); }
    @Override public void insertCertTestMethod(Long id, RestJsonWrapperBean wrapper) { dao.insertCertTestMethods(id, wrapper); }
    @Override public void deleteEmpCertTestMethod(RestJsonWrapperBean wrapper) { dao.deleteCertTestMethods(wrapper); }
    @Override public Page<EmcEmpBean> selectEditReportEmpPagination(RestJsonWrapperBean wrapper) { return dao.readReportEditors(wrapper); }
    @Override public Page<EmcEmpBean> selectSampleEmpPagination(RestJsonWrapperBean wrapper) { return dao.readSampleEmployees(wrapper); }

    private static UnsupportedOperationException unsupported() {
        return new UnsupportedOperationException("Personnel write or auxiliary flow is not implemented");
    }
}
