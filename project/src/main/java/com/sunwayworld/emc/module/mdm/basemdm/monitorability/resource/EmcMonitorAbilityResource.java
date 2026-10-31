package com.sunwayworld.emc.module.mdm.basemdm.monitorability.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.basemdm.monitorability.bean.EmcMonitorAbilityBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitorability.bean.EmcMonitorAbilityEmpBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitorability.bean.EmcMonitorAbilityEquiptBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitorability.bean.EmcMonitorAbilityTestBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitorability.service.EmcMonitorAbilityService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;

/**
 * @author magt@sunwayworld.com
 * @date 2019-10-09
 */
@RequestMapping("/secure/emc/module/mdm/basemdm/monitor-abilities")
public interface EmcMonitorAbilityResource extends GenericResource<EmcMonitorAbilityService, EmcMonitorAbilityBean, Long>, GenericChoosableResource<EmcMonitorAbilityService, EmcMonitorAbilityBean, Long> {
        
        Long insert(RestJsonWrapperBean wrapper);
        
        Long insertTestItem(Long moniAbilId, RestJsonWrapperBean wrapper);
        
        Long insertEquiptItem(Long moniAbilId, RestJsonWrapperBean wrapper);
        
        Long insertEmpItem(Long moniAbilId, RestJsonWrapperBean wrapper);
        
        Page<EmcMonitorAbilityTestBean> selectTestPaginationByMoniAbilId(Long id, RestJsonWrapperBean wrapper);
        
        Page<EmcMonitorAbilityEquiptBean> selectEquiptPaginationByMoniAbilId(Long id, RestJsonWrapperBean wrapper);
        
        Page<EmcMonitorAbilityEmpBean> selectEmpPaginationByMoniAbilId(Long id, RestJsonWrapperBean wrapper);
        
        void deleteTestItem(RestJsonWrapperBean wrapper);
        
        void deleteEquiptItem(RestJsonWrapperBean wrapper);
        
        void deleteEmpItem(RestJsonWrapperBean wrapper);
        
        void syncEquipt(RestJsonWrapperBean wrapper);
        
        void syncEmp(RestJsonWrapperBean wrapper);
}
