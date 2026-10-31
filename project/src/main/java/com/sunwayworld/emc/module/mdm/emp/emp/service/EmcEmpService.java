package com.sunwayworld.emc.module.mdm.emp.emp.service;

import org.springframework.web.bind.annotation.PathVariable;

import com.sunwayworld.emc.module.mdm.basemdm.cert.bean.EmcCertBean;
import com.sunwayworld.emc.module.mdm.basemdm.cert.bean.EmcCertTestMethodBean;
import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpBean;
import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpQualBean;
import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpTestBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;

/**
 * @author yuanhh@sunwayworld.com 2019年9月4日
 */
public interface EmcEmpService extends GenericService<EmcEmpBean, String>, GenericChoosableService<EmcEmpBean, String>, GenericSelectableService<EmcEmpBean, String> {
    Long insertQual(String id, RestJsonWrapperBean wrapper);

    void insertTest(String id, RestJsonWrapperBean wrapper);

    Page<EmcEmpQualBean> selectQualPagationByEmp(String id, RestJsonWrapperBean wrapper);

    Page<EmcEmpTestBean> selectTestPagationByEmp(String id, RestJsonWrapperBean wrapper);

    void deleteQual(String id, RestJsonWrapperBean wrapper);

    void deleteTest(String id, RestJsonWrapperBean wrapper);

    Page<EmcCertBean> selectCretPagationByEmp(@PathVariable String id, RestJsonWrapperBean wrapper);

    Page<EmcCertTestMethodBean> selectCertTestMethodPagationByEmp(@PathVariable Long id, RestJsonWrapperBean wrapper);

    void insertCertTestMethod(@PathVariable Long id, RestJsonWrapperBean wrapper);

    void deleteEmpCertTestMethod(RestJsonWrapperBean wrapper);

    Page<EmcEmpBean> selectEditReportEmpPagination(RestJsonWrapperBean wrapper);

    Page<EmcEmpBean> selectSampleEmpPagination(RestJsonWrapperBean wrapper);
}
