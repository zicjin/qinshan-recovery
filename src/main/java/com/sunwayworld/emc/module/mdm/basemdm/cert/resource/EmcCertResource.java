package com.sunwayworld.emc.module.mdm.basemdm.cert.resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.basemdm.cert.bean.EmcCertBean;
import com.sunwayworld.emc.module.mdm.basemdm.cert.bean.EmcCertLogBean;
import com.sunwayworld.emc.module.mdm.basemdm.cert.bean.EmcCertMonitorAbilityBean;
import com.sunwayworld.emc.module.mdm.basemdm.cert.bean.EmcCertTestMethodBean;
import com.sunwayworld.emc.module.mdm.basemdm.cert.service.EmcCertService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-08
 */
@RequestMapping("/secure/emc/module/mdm/basemdm/certs")
public interface EmcCertResource extends GenericResource<EmcCertService, EmcCertBean, Long> {
    Long updateCertMethodAuth(@PathVariable Long id, RestJsonWrapperBean wrapper);
    
    Long insert(@PathVariable String id, RestJsonWrapperBean wrapper);
    
    void insertCertTestMethod(@PathVariable Long id, RestJsonWrapperBean wrapper);
    
    void insertCertMonitorAbility(@PathVariable Long certId, RestJsonWrapperBean wrapper);
    
    Page<EmcCertBean> selectCertPaginationById(@PathVariable String id, RestJsonWrapperBean wrapper);
    
    Page<EmcCertTestMethodBean> selectCertTestMethodPaginationById(@PathVariable Long id, RestJsonWrapperBean wrapper);
    
    Page<EmcCertMonitorAbilityBean> selectCertMonitorAbilityById(@PathVariable Long id, RestJsonWrapperBean wrapper);
    
    Page<EmcCertLogBean> selectCertLogById(@PathVariable Long id, RestJsonWrapperBean wrapper);
    
    void copyCertTestMethod(RestJsonWrapperBean wrapper);
    
    Page<EmcCertBean> selectChoosableMonitorObjectPagination(RestJsonWrapperBean wrapper);
    
    void deleteEmpCert(RestJsonWrapperBean wrapper);
    
    void deleteEmpCertTestMethod(RestJsonWrapperBean wrapper);
    
    void deleteEmpCertMonitorAbility(RestJsonWrapperBean wrapper);

}
