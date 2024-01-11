package com.sunwayworld.emc.module.mdm.bizmdm.client.resource.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcClientBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcClientContactBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcMonitorPointBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcMonitorPointSchemeBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.resource.EmcClientResource;
import com.sunwayworld.emc.module.mdm.bizmdm.client.service.EmcClientService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author magh@sunwayworld.com
 * @date 2019-10-19
 */

@LogModule("客户")
@RestController
@GikamBean
public class EmcClientResourceImpl implements EmcClientResource {
    @Autowired
    private EmcClientService clientService;

    @Override
    public EmcClientService getService() {
        return clientService;
    }

    @Log(value = "新增客户", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

    @Override
    @Log(value = "根据客户ID查询联系人", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/contacts/queries", method = RequestMethod.POST)
    public Page<EmcClientContactBean> selectContactPaginationByClientId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().selectContactPaginationByClientId(id, wrapper);
    }

    @Override
    @Log(value = "根据客户ID查询监测点", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/monitor-points/queries", method = RequestMethod.POST)
    public Page<EmcMonitorPointBean> selectMonitorPointPaginationByClientId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().selectMonitorPointPaginationByClientId(id, wrapper);
    }

    @Override
    @Log(value = "根据监测点ID查询方案", type = LogType.SELECT)
    @RequestMapping(value = "/monitor-points/{monitorPointId}/schemes/queries", method = RequestMethod.POST)
    public Page<EmcMonitorPointSchemeBean> selectSchemePaginationByMonitorPointId(@PathVariable Long monitorPointId, RestJsonWrapperBean wrapper) {
        return getService().selectSchemePaginationByMonitorPointId(monitorPointId, wrapper);
    }

    @Override
    @Log(value = "新增联系人", type = LogType.INSERT)
    @RequestMapping(value = "/{id}/contacts", method = RequestMethod.POST)
    public void insertContact(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        getService().insertContact(id, wrapper);
    }

    @Override
    @Log(value = "新增监测点", type = LogType.INSERT)
    @RequestMapping(value = "/{id}/monitor-points", method = RequestMethod.POST)
    public void insertMonitorPoint(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        getService().insertMonitorPoint(id, wrapper);
    }

    @Override
    @Log(value = "新增监测点方案", type = LogType.INSERT)
    @RequestMapping(value = "/monitor-points/{monitorPointId}/schemes", method = RequestMethod.POST)
    public void insertMonitorPointScheme(@PathVariable Long monitorPointId, RestJsonWrapperBean wrapper) {
        getService().insertMonitorPointScheme(monitorPointId, wrapper);
    }

    @Override
    @Log(value = "监测点选择页查询", type = LogType.SELECT)
    @RequestMapping(value = "/monitor-points/queries/choosable", method = RequestMethod.POST)
    public Page<EmcMonitorPointBean> selectMonitorPointChoosablePagination(RestJsonWrapperBean wrapper) {
        return getService().selectMonitorPointChoosablePagination(wrapper);
    }

    @Override
    @Log(value = "删除联系人", type = LogType.DELETE)
    @RequestMapping(value = "/contacts", method = RequestMethod.DELETE)
    public void deleteContact(RestJsonWrapperBean wrapper) {
        getService().deleteContact(wrapper);
    }

    @Override
    @Log(value = "删除监测点", type = LogType.DELETE)
    @RequestMapping(value = "/monitor-points", method = RequestMethod.DELETE)
    public void deleteMonitorPoint(RestJsonWrapperBean wrapper) {
        getService().deleteMonitorPoint(wrapper);
    }

    @Override
    @Log(value = "删除监测点方案", type = LogType.DELETE)
    @RequestMapping(value = "/monitor-points/schemes", method = RequestMethod.DELETE)
    public void deleteMonitorPointScheme(RestJsonWrapperBean wrapper) {
        getService().deleteMonitorPointScheme(wrapper);
    }

    @Override
    @Log(value = "设置监测点指标", type = LogType.UPDATE)
    @RequestMapping(value = "/monitor-points/{monitorPointId}/index-set", method = RequestMethod.PUT)
    public void insertIndex(@PathVariable Long monitorPointId, RestJsonWrapperBean wrapper) {
        getService().insertMonitorPointIndex(wrapper);
    }

    @Override
    @Log(value = "复制监测点方案", type = LogType.INSERT)
    @RequestMapping(value = "/monitor-points/{monitorPointId}/scheme-cope", method = RequestMethod.POST)
    public void copyScheme(@PathVariable Long monitorPointId, RestJsonWrapperBean wrapper) {
        getService().copyMonitorPointScheme(monitorPointId, wrapper);
    }

    @Override
    @Log(value = "复制监测点指标", type = LogType.UPDATE)
    @RequestMapping(value = "/monitor-points/{monitorPointId}/index-cope", method = RequestMethod.PUT)
    public void copyIndex(@PathVariable Long monitorPointId, RestJsonWrapperBean wrapper) {
        getService().copyMonitorPointIndex(wrapper);
    }

    @Override
    @Log(value = "监测点方案选择页查询", type = LogType.SELECT)
    @RequestMapping(value = "/monitor-points/schemes/queries/choosable", method = RequestMethod.POST)
    public Page<EmcMonitorPointSchemeBean> selectMonitorPointSchemeChoosablePagination(RestJsonWrapperBean wrapper) {
        return getService().selectMonitorPointSchemeChoosablePagination(wrapper);
    }

    @Override
    @Log(value = "过滤监测点类型选择页面", type = LogType.SELECT)
    @RequestMapping(value = "/monitor-points/queries/selectable", method = RequestMethod.GET)
    public List<EmcMonitorPointBean> selectFilterPointCategorySelectableList() {
        return getService().selectFilterPointCategorySelectableList();
    }

    @Override
    @Log(value = "根据受检单位查询监测点", type = LogType.SELECT)
    @RequestMapping(value = "/queries/monitor-points", method = RequestMethod.POST)
    public Page<EmcMonitorPointBean> selectMonitorPointByTestOrgId(RestJsonWrapperBean wrapper) {
        return getService().selectMonitorPointByTestOrgId(wrapper);
    }

    @Override
    @Log(value = "联系人选择页查询", type = LogType.SELECT)
    @RequestMapping(value = "/contacts/queries/choosable", method = RequestMethod.POST)
    public Page<EmcClientContactBean> selectContactChoosablePagination(RestJsonWrapperBean wrapper) {
        return getService().selectContactChoosablePagination(wrapper);
    }

    @Override
    @Log(value = "复制监测点", type = LogType.INSERT)
    @RequestMapping(value = "/monitor-points/copy", method = RequestMethod.POST)
    public void copyMonitorPoints(RestJsonWrapperBean wrapper) {
        getService().copyMonitorPoint(wrapper);
    }

    @Override
    @Log(value = "监测地点选择页查询", type = LogType.SELECT)
    @RequestMapping(value = "/sub-clients/queries/choosable", method = RequestMethod.POST)
    public Page<EmcClientBean> selectSubClientChoosablePagination(RestJsonWrapperBean wrapper) {
        return getService().selectSubClientChoosablePagination(wrapper);
    }
}
