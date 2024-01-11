package com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.resource.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorSchemeBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.resource.EmcMonitorTmplResource;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorSchemeService;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorTmplService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-19
 */

@LogModule("监测模板")
@RestController
@GikamBean
public class EmcMonitorTmplResourceImpl implements EmcMonitorTmplResource {
    @Autowired
    private EmcMonitorTmplService monitorTmplService;
    @Autowired
    private EmcMonitorSchemeService monitorSchemeService;

    @Override
    public EmcMonitorTmplService getService() {
        return monitorTmplService;
    }

    @Log(value = "新增监测模板", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public String insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

    @Override
    @Log(value = "复制监测模板", type = LogType.INSERT)
    @RequestMapping(value = "/action/copy", method = RequestMethod.POST)
    public void copy(RestJsonWrapperBean jsonWrapper) {
        getService().copy(jsonWrapper);
    }

    @Override
    @Log(value = "根据监测模板ID查询监测方案", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/schemes/queries", method = RequestMethod.POST)
    public Page<EmcMonitorSchemeBean> selectSchemePaginationByTmplId(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return monitorSchemeService.selectPaginationByFilter(SearchFilter.instance().match("MONITORTMPLID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Log(value = "新增监测方案", type = LogType.INSERT)
    @RequestMapping(value = "/{id}/schemes", method = RequestMethod.POST)
    public Long insertScheme(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return monitorSchemeService.insert(id, wrapper);
    }

    @Override
    @Log(value = "删除监测方案", type = LogType.DELETE)
    @RequestMapping(value = "/{id}/schemes", method = RequestMethod.DELETE)
    public void deleteScheme(@PathVariable String id, RestJsonWrapperBean wrapper) {
        monitorSchemeService.delete(wrapper);
    }

    @Override
    @Log(value = "编辑监测方案", type = LogType.INSERT)
    @RequestMapping(value = "/{id}/schemes/{schemeName}/action/edit", method = RequestMethod.POST)
    public void editScheme(@PathVariable String id, @PathVariable String schemeName, RestJsonWrapperBean wrapper) {
        monitorSchemeService.edit(id, schemeName, wrapper);
    }

    @Override
    @Log(value = "新增测试", type = LogType.INSERT)
    @RequestMapping(value = "/{id}/schemes/{schemeName}/action/add-test", method = RequestMethod.POST)
    public void addSchemeTest(@PathVariable String id, @PathVariable String schemeName, RestJsonWrapperBean wrapper) {
        monitorSchemeService.addSchemeTest(id, schemeName, wrapper);
    }

    @Override
    @Log(value = "删除测试", type = LogType.DELETE)
    @RequestMapping(value = "/{id}/schemes/{schemeName}/action/delete-test", method = RequestMethod.DELETE)
    public void deleteSchemeTest(@PathVariable String id, @PathVariable String schemeName, RestJsonWrapperBean wrapper) {
        monitorSchemeService.deleteSchemeTest(id, schemeName, wrapper);
    }

    @Override
    @Log(value = "监测方案下拉框数据查询", type = LogType.SELECT)
    @RequestMapping(value = "/schemes/queries/selectable", method = RequestMethod.GET)
    public List<EmcMonitorSchemeBean> selectSchemeSelectableList() {
        return monitorSchemeService.selectSelectableList();
    }

    @Override
    @Log(value = "监测方案选择页查询", type = LogType.SELECT)
    @RequestMapping(value = "/schemes/queries/choosable", method = RequestMethod.POST)
    public Page<EmcMonitorSchemeBean> selectSchemeChoosablePagination(RestJsonWrapperBean wrapper) {
        return getService().selectSchemeChoosablePagination(wrapper);
    }

    @Override
    @Log(value = "监测方案选择页查询报价单", type = LogType.SELECT)
    @RequestMapping(value = "/schemes/queries/detectionchoosable", method = RequestMethod.POST)
    public Page<EmcMonitorSchemeBean> selectSchemeDetectionChoosablePagination(RestJsonWrapperBean wrapper) {
        return getService().selectSchemeDetectionChoosablePagination(wrapper);
    }

    @Override
    @Log(value = "监测方案分析项组选择页查询", type = LogType.SELECT)
    @RequestMapping(value = "/scheme-item-groups/queries/choosable", method = RequestMethod.POST)
    public Page<EmcMonitorSchemeBean> selectSchemeItemGroupChoosablePagination(RestJsonWrapperBean wrapper) {
        return getService().selectSchemeItemGroupChoosablePagination(wrapper);
    }
    
    @Override
    @Log(value = "监测方案选择页查询报价单去重", type = LogType.SELECT)
    @RequestMapping(value = "/schemes/queries/detectionchoosabledeweight", method = RequestMethod.POST)
    public Page<EmcMonitorSchemeBean> selectDetectionChoosablePaginationDeWeight(RestJsonWrapperBean wrapper) {
        return getService().selectDetectionChoosablePaginationDeWeight(wrapper);
    }

}
