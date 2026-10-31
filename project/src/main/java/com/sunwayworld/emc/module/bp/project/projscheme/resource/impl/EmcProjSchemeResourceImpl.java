package com.sunwayworld.emc.module.bp.project.projscheme.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjDTO;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjOrderBean;
import com.sunwayworld.emc.module.bp.project.projscheme.resource.EmcProjSchemeResource;
import com.sunwayworld.emc.module.bp.project.projscheme.service.EmcProjSchemeService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-10-08
 */

@LogModule("监测方案表")
@RestController
@GikamBean
public class EmcProjSchemeResourceImpl implements EmcProjSchemeResource {
    @Autowired
    private EmcProjSchemeService projSchemeService;

    @Override
    public EmcProjSchemeService getService() {
        return projSchemeService;
    }

    @Log(value = "通过监测方案查询样品", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/{id}/proj-orders/queries", method = RequestMethod.POST)
    public Page<EmcProjOrderBean> selectProjOrderPagationByProjScheme(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return projSchemeService.selectProjOrderPagationByProjScheme(id, wrapper);
    }

    @Log(value = "查询样品", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/proj-orders/queries", method = RequestMethod.POST)
    public Page<EmcProjOrderBean> selectOrderPagation(RestJsonWrapperBean wrapper) {
        return projSchemeService.selectOrderPagation(wrapper);
    }

    @Log(value = "通过项目编码查询方案", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/projects/{id}/queries", method = RequestMethod.POST)
    public Page<EmcProjDTO> selectPagationByProjId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return projSchemeService.selectByProjId(id);
    }

    @Override
    @Log(value = "复制份", type = LogType.INSERT)
    @RequestMapping(value = "/action/copy", method = RequestMethod.POST)
    public void copy(RestJsonWrapperBean wrapper) {
        projSchemeService.copy(wrapper);
    }

    @Override
    @Log(value = "复制点", type = LogType.INSERT)
    @RequestMapping(value = "/action/copy-point", method = RequestMethod.POST)
    public void copyPoint(RestJsonWrapperBean wrapper) {
        projSchemeService.copyPoint(wrapper);
    }

    @Log(value = "批量修改频次", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/action/update-frequency", method = RequestMethod.PUT)
    public void updateFrequency(RestJsonWrapperBean wrapper) {
        projSchemeService.updateFrequency(wrapper);
    }

    @Override
    @Log(value = "编辑测试分析项组", type = LogType.INSERT)
    @RequestMapping(value = "/action/proj-orders/update-item-group", method = RequestMethod.POST)
    public void updateProjOrderItemGroup(RestJsonWrapperBean wrapper) {
        projSchemeService.updateProjOrderItemGroup(wrapper);
    }

    @Log(value = "设置分包商", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/proj-orders/action/subcontractor-edit", method = RequestMethod.PUT)
    public void editSubcontractor(RestJsonWrapperBean wrapper) {
        getService().editSubcontractor(wrapper);
    }

    @Override
    @Log(value = "初始化新增", type = LogType.INSERT)
    @RequestMapping(value = "/action/init-insert", method = RequestMethod.POST)
    public void initInsert(RestJsonWrapperBean wrapper) {
        projSchemeService.initInsert(wrapper);
    }

    @Override
    @Log(value = "更新样品指标", type = LogType.INSERT)
    @RequestMapping(value = "/action/update-order-index", method = RequestMethod.POST)
    public void updateOrderIndex(RestJsonWrapperBean wrapper) {
        getService().updateOrderIndex(wrapper);
    }

    @Log(value = "关联方案更新分包商", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/proj-orders/action/relevance-subcontractor-update", method = RequestMethod.POST)
    public void relevanceUpdateSubcontractor(RestJsonWrapperBean wrapper) {
        getService().relevanceUpdateSubcontractor(wrapper);
    }

    @Log(value = "关联方案删除分包商", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/proj-orders/action/relevance-subcontractor-delete", method = RequestMethod.POST)
    public void relevanceDeleteSubcontractor(RestJsonWrapperBean wrapper) {
        getService().relevanceDeleteSubcontractor(wrapper);
    }
}
