package com.sunwayworld.emc.module.mdm.basemdm.cert.resource.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.basemdm.cert.bean.EmcCertBean;
import com.sunwayworld.emc.module.mdm.basemdm.cert.bean.EmcCertLogBean;
import com.sunwayworld.emc.module.mdm.basemdm.cert.bean.EmcCertMonitorAbilityBean;
import com.sunwayworld.emc.module.mdm.basemdm.cert.bean.EmcCertTestMethodBean;
import com.sunwayworld.emc.module.mdm.basemdm.cert.resource.EmcCertResource;
import com.sunwayworld.emc.module.mdm.basemdm.cert.service.EmcCertLogService;
import com.sunwayworld.emc.module.mdm.basemdm.cert.service.EmcCertMonitorAbilityService;
import com.sunwayworld.emc.module.mdm.basemdm.cert.service.EmcCertService;
import com.sunwayworld.emc.module.mdm.basemdm.cert.service.EmcCertTestMethodService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-08
 */

@LogModule("人员证书表")
@RestController
@GikamBean
public class EmcCertResourceImpl implements EmcCertResource {
    
    @Autowired
    private EmcCertService certService;
    
    @Autowired
    private EmcCertTestMethodService certTestMethodService;
    
    @Autowired
    private EmcCertMonitorAbilityService certMonitorAbilityService;
    
    @Autowired
    private EmcCertLogService certLogService;

    @Override
    public EmcCertService getService() {
        return certService;
    }
    
    @Log(value = "人员证书授权", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/{id}/cert-method-auth", method = RequestMethod.POST)
    public Long updateCertMethodAuth(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().updateCertMethodAuth(id, wrapper);
    }

    @Log(value = "新增人员证书表", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public Long insert(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return getService().insert(id, wrapper);
    }
    
    @Log(value = "新增方法内容", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/{id}/cert-test-methods", method = RequestMethod.PUT)
    public void insertCertTestMethod(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        certTestMethodService.insert(id, wrapper);
    }
    
    @Log(value = "新增监测能力表", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/{certId}/cert-monitor-abilities", method = RequestMethod.PUT)
    public void insertCertMonitorAbility(@PathVariable Long certId, RestJsonWrapperBean wrapper) {
        certMonitorAbilityService.insert(certId, wrapper);
    }
    
    @Override
    @Log(value = "查询证书列表基本信息", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/queries", method = RequestMethod.POST)
    public Page<EmcCertBean> selectCertPaginationById(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return getService().selectPaginationByFilter(SearchFilter.instance().match("EMPID", id).filter(MatchPattern.EQ), wrapper);
    }
    
    @Override
    @Log(value = "查询方法内容基本信息", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/cert-test-methods/queries", method = RequestMethod.POST)
    public Page<EmcCertTestMethodBean> selectCertTestMethodPaginationById(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return certTestMethodService.selectPaginationByFilter(SearchFilter.instance().match("CERTID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Log(value = "查询检测能力基本信息", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/cert-monitor-abilities/queries", method = RequestMethod.POST)
    public Page<EmcCertMonitorAbilityBean> selectCertMonitorAbilityById(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return certMonitorAbilityService.selectPaginationByFilter(SearchFilter.instance().match("CERTID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Log(value = "查询审核历史基本信息", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/cert-logs/queries", method = RequestMethod.POST)
    public Page<EmcCertLogBean> selectCertLogById(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return certLogService.selectPaginationByFilter(SearchFilter.instance().match("CERTID", id).filter(MatchPattern.EQ), wrapper);
    }
    
    @Override
    @Log(value = "复制到其他测试方法", type = LogType.INSERT)
    @RequestMapping(value = "/test-methods/action/copy", method = RequestMethod.POST)
    public void copyCertTestMethod(RestJsonWrapperBean wrapper) {
        certTestMethodService.copyCertTestMethod(wrapper);
    }
    
    @Override
    @Log(value = "方法内容选择页面", type = LogType.SELECT)
    @RequestMapping(value = "/certs/queries/choosable", method = RequestMethod.POST)
    public Page<EmcCertBean> selectChoosableMonitorObjectPagination(RestJsonWrapperBean wrapper) {
        return getService().selectChoosablePagination(wrapper);
    }
    
    @Override
    @Log(value = "删除证书列表", type = LogType.DELETE)
    @RequestMapping(value = "/certs", method = RequestMethod.DELETE)
    public void deleteEmpCert(RestJsonWrapperBean wrapper) {
        getService().delete(wrapper);
    }
    
    @Override
    @Log(value = "删除方法内容", type = LogType.DELETE)
    @RequestMapping(value = "/cert-test-methods", method = RequestMethod.DELETE)
    public void deleteEmpCertTestMethod(RestJsonWrapperBean wrapper) {
        certTestMethodService.delete(wrapper);
    }
    
    @Override
    @Log(value = "删除监测能力", type = LogType.DELETE)
    @RequestMapping(value = "/cert-monitor-abilities", method = RequestMethod.DELETE)
    public void deleteEmpCertMonitorAbility(RestJsonWrapperBean wrapper) {
        certMonitorAbilityService.delete(wrapper);
    }
}
