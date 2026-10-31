package com.sunwayworld.emc.module.mdm.basemdm.monitorability.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.basemdm.monitorability.bean.EmcMonitorAbilityEmpBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitorability.bean.EmcMonitorAbilityEquiptBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitorability.bean.EmcMonitorAbilityTestBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitorability.resource.EmcMonitorAbilityResource;
import com.sunwayworld.emc.module.mdm.basemdm.monitorability.service.EmcMonitorAbilityEmpService;
import com.sunwayworld.emc.module.mdm.basemdm.monitorability.service.EmcMonitorAbilityEquiptService;
import com.sunwayworld.emc.module.mdm.basemdm.monitorability.service.EmcMonitorAbilityService;
import com.sunwayworld.emc.module.mdm.basemdm.monitorability.service.EmcMonitorAbilityTestService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
/**
 * @author magt@sunwayworld.com
 * @date 2019-10-09
 */

@LogModule("监测能力")
@RestController
@GikamBean
public class EmcMonitorAbilityResourceImpl implements EmcMonitorAbilityResource {
    @Autowired
    private EmcMonitorAbilityService monitorAbilityService;
    
    @Autowired
    private EmcMonitorAbilityTestService monitorAbilityTestService;
    
    @Autowired
    private EmcMonitorAbilityEquiptService monitorAbilityEquiptService;

    @Autowired
    private EmcMonitorAbilityEmpService monitorAbilityEmpService;
    
    @Override
    public EmcMonitorAbilityService getService() {
        return monitorAbilityService;
    }
    
    
    @Log(value = "新增监测能力", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

    @Override
    @Log(value = "插入关联测试方法", type = LogType.INSERT)
    @RequestMapping(value = "/{moniAbilId}/tests/insert", method = RequestMethod.POST)
    public Long insertTestItem(@PathVariable Long moniAbilId, RestJsonWrapperBean wrapper) {
        return monitorAbilityTestService.insert(moniAbilId, wrapper);
    }
    
    @Override
    @Log(value = "插入关联仪器", type = LogType.INSERT)
    @RequestMapping(value = "/{moniAbilId}/equipts/insert", method = RequestMethod.POST)
    public Long insertEquiptItem(@PathVariable Long moniAbilId, RestJsonWrapperBean wrapper) {
        return monitorAbilityEquiptService.insert(moniAbilId, wrapper);
    }
    
    @Override
    @Log(value = "插入关联测试", type = LogType.INSERT)
    @RequestMapping(value = "/{moniAbilId}/emps/insert", method = RequestMethod.POST)
    public Long insertEmpItem(@PathVariable Long moniAbilId, RestJsonWrapperBean wrapper) {
        return monitorAbilityEmpService.insert(moniAbilId, wrapper);
    }
    
    @Override
    @Log(value = "根据监测能力ID查询关联测试", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/tests/queries", method = RequestMethod.POST)
    public Page<EmcMonitorAbilityTestBean> selectTestPaginationByMoniAbilId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return monitorAbilityTestService.selectPaginationByFilter(SearchFilter.instance().match("MONITORABILITYID", id).filter(MatchPattern.EQ), wrapper);
    }
    
    @Override
    @Log(value = "根据监测能力ID查询关联仪器", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/equipts/queries", method = RequestMethod.POST)
    public Page<EmcMonitorAbilityEquiptBean> selectEquiptPaginationByMoniAbilId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return monitorAbilityEquiptService.selectPaginationByFilter(SearchFilter.instance().match("MONITORABILITYID", id).filter(MatchPattern.EQ), wrapper);
    }
    
    @Override
    @Log(value = "根据监测能力ID查询关联人员", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/emps/queries", method = RequestMethod.POST)
    public Page<EmcMonitorAbilityEmpBean> selectEmpPaginationByMoniAbilId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return monitorAbilityEmpService.selectPaginationByFilter(SearchFilter.instance().match("MONITORABILITYID", id).filter(MatchPattern.EQ), wrapper);
    }
    
    @Override
    @Log(value = "删除监测能力关联测试", type = LogType.DELETE)
    @RequestMapping(value = "/tests", method = RequestMethod.DELETE)
    public void deleteTestItem(RestJsonWrapperBean wrapper) {
        monitorAbilityTestService.delete(wrapper);
    }
    
    @Override
    @Log(value = "删除监测能力关联仪器", type = LogType.DELETE)
    @RequestMapping(value = "/equipts", method = RequestMethod.DELETE)
    public void deleteEquiptItem(RestJsonWrapperBean wrapper) {
        monitorAbilityEquiptService.delete(wrapper);
    }
    
    @Override
    @Log(value = "删除监测能力关联人员", type = LogType.DELETE)
    @RequestMapping(value = "/emps", method = RequestMethod.DELETE)
    public void deleteEmpItem(RestJsonWrapperBean wrapper) {
        monitorAbilityEmpService.delete(wrapper);
    }


    @Override
    @Log(value = "同步仪器信息", type = LogType.INSERT)
    @RequestMapping(value = "/sync-equipt", method = RequestMethod.POST)
    public void syncEquipt(RestJsonWrapperBean wrapper) {
        monitorAbilityEquiptService.syncEquipt(wrapper);
    }
    
    @Override
    @Log(value = "同步人员信息", type = LogType.INSERT)
    @RequestMapping(value = "/sync-emp", method = RequestMethod.POST)
    public void syncEmp(RestJsonWrapperBean wrapper) {
        monitorAbilityEmpService.syncEmp(wrapper);
    }
}
