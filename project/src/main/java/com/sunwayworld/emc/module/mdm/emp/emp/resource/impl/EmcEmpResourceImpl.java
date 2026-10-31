package com.sunwayworld.emc.module.mdm.emp.emp.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.basemdm.cert.bean.EmcCertBean;
import com.sunwayworld.emc.module.mdm.basemdm.cert.bean.EmcCertTestMethodBean;
import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpBean;
import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpQualBean;
import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpTestBean;
import com.sunwayworld.emc.module.mdm.emp.emp.resource.EmcEmpResource;
import com.sunwayworld.emc.module.mdm.emp.emp.service.EmcEmpService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author yuanhh@sunwayworld.com 2019年9月4日
 */
@LogModule(value = "人员管理")
@RestController
@GikamBean
public class EmcEmpResourceImpl implements EmcEmpResource {
    @Autowired
    private EmcEmpService empService;

    @Override
    public EmcEmpService getService() {
        return empService;
    }

    @Override
    @RequestMapping(method = RequestMethod.POST)
    public String insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public void save(@PathVariable String id, RestJsonWrapperBean wrapper) {
        getService().save(id, wrapper);
    }

    @Override
    @RequestMapping(method = RequestMethod.DELETE)
    public void delete(RestJsonWrapperBean wrapper) {
        getService().delete(wrapper);
    }

    @Log(value = "新增资格证书", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/{id}/quals", method = RequestMethod.POST)
    public Long insertQual(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return getService().insertQual(id, wrapper);
    }

    @Override
    @Log(value = "删除资格证书", type = LogType.DELETE)
    @RequestMapping(value = "/{id}/quals", method = RequestMethod.DELETE)
    public void deleteQual(@PathVariable String id, RestJsonWrapperBean wrapper) {
        getService().deleteQual(id, wrapper);
    }

    @Log(value = "新增可见测试", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/{id}/tests", method = RequestMethod.POST)
    public void insertTest(@PathVariable String id, RestJsonWrapperBean wrapper) {
        getService().insertTest(id, wrapper);
    }

    @Override
    @Log(value = "删除可见测试", type = LogType.DELETE)
    @RequestMapping(value = "/{id}/tests", method = RequestMethod.DELETE)
    public void deleteTest(@PathVariable String id, RestJsonWrapperBean wrapper) {
        getService().deleteTest(id, wrapper);
    }

    @Log(value = "通过人员查询资格证书", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/{id}/quals/queries", method = RequestMethod.POST)
    public Page<EmcEmpQualBean> selectQualPagationByEmp(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return getService().selectQualPagationByEmp(id, wrapper);
    }

    @Log(value = "通过人员查询可见测试", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/{id}/tests/queries", method = RequestMethod.POST)
    public Page<EmcEmpTestBean> selectTestPagationByEmp(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return getService().selectTestPagationByEmp(id, wrapper);
    }

    @Log(value = "通过人员查询分析员上岗证", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/{id}/credentials/queries", method = RequestMethod.POST)
    public Page<EmcCertBean> selectCretPagationByEmp(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return getService().selectCretPagationByEmp(id, wrapper);
    }

    @Log(value = "通过上岗证查询方法", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/{id}/methods/queries", method = RequestMethod.POST)
    public Page<EmcCertTestMethodBean> selectCertTestMethodPagationByEmp(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().selectCertTestMethodPagationByEmp(id, wrapper);
    }

    @Log(value = "新增方法内容", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/{id}/cert-test-methods", method = RequestMethod.PUT)
    public void insertCertTestMethod(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        getService().insertCertTestMethod(id, wrapper);
    }

    @Override
    @Log(value = "删除方法内容", type = LogType.DELETE)
    @RequestMapping(value = "/cert-test-methods", method = RequestMethod.DELETE)
    public void deleteEmpCertTestMethod(RestJsonWrapperBean wrapper) {
        getService().deleteEmpCertTestMethod(wrapper);
    }

    @Override
    @Log(value = "报告编制人选择页查询", type = LogType.SELECT)
    @RequestMapping(value = "/report-users/queries/choosable", method = RequestMethod.POST)
    public Page<EmcEmpBean> selectEditReportEmpPagination(RestJsonWrapperBean wrapper) {
        return getService().selectEditReportEmpPagination(wrapper);
    }
    
    @Override
    @Log(value = "采样人选择页查询", type = LogType.SELECT)
    @RequestMapping(value = "/sample/queries/choosable", method = RequestMethod.POST)
    public Page<EmcEmpBean> selectSampleEmpPagination(RestJsonWrapperBean wrapper) {
        return getService().selectSampleEmpPagination(wrapper);
    }
}
