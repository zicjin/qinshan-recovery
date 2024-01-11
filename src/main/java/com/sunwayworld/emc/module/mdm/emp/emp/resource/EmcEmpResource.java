package com.sunwayworld.emc.module.mdm.emp.emp.resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.basemdm.cert.bean.EmcCertBean;
import com.sunwayworld.emc.module.mdm.basemdm.cert.bean.EmcCertTestMethodBean;
import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpBean;
import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpQualBean;
import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpTestBean;
import com.sunwayworld.emc.module.mdm.emp.emp.service.EmcEmpService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableResource;

/**
 * @author yuanhh@sunwayworld.com 2019年9月4日
 */
@RequestMapping("/secure/emc/module/mdm/emp/emps")
public interface EmcEmpResource extends GenericResource<EmcEmpService, EmcEmpBean, String>, GenericChoosableResource<EmcEmpService, EmcEmpBean, String>, GenericSelectableResource<EmcEmpService, EmcEmpBean, String> {

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
