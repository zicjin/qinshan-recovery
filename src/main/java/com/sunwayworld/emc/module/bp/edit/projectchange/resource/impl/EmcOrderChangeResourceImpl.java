package com.sunwayworld.emc.module.bp.edit.projectchange.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcOrderItemGroupChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.resource.EmcOrderChangeResource;
import com.sunwayworld.emc.module.bp.edit.projectchange.service.EmcOrderChangeService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule("样品")
@RestController
@GikamBean
public class EmcOrderChangeResourceImpl implements EmcOrderChangeResource {

    @Autowired
    private EmcOrderChangeService orderService;

    @Override
    public EmcOrderChangeService getService() {
        return orderService;
    }

    @Log(value = "编辑样品", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/action/edit", method = RequestMethod.PUT)
    public void edit(RestJsonWrapperBean wrapper) {
        getService().edit(wrapper);
    }

    @Log(value = "现场采样-采样准备-样品-编辑测试", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/action/edit-tests", method = RequestMethod.PUT)
    public void editTest(RestJsonWrapperBean wrapper) {
        getService().editTest(wrapper);
    }

    @Log(value = "编辑分析项", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/item-groups/{orderItemGroupId}/action/edit-item", method = RequestMethod.PUT)
    public void editItem(@PathVariable Long orderItemGroupId, RestJsonWrapperBean wrapper) {
        getService().editItem(orderItemGroupId, wrapper);
    }

    @Log(value = "终止/取消终止样品", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/action/terminate-order", method = RequestMethod.PUT)
    public void terminateOrder(RestJsonWrapperBean wrapper) {
        orderService.terminateOrder(wrapper);
    }

    @Log(value = "通过样品查询分析项组", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/{id}/item-groups/queries", method = RequestMethod.POST)
    public Page<EmcOrderItemGroupChangeBean> selectItemGroupPagationByOrder(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().selectItemGroupPagationByOrder(id, wrapper);
    }

    @Log(value = "通过样品分析项组查询分析项", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/item-groups/{orderItemGroupId}/items/queries", method = RequestMethod.POST)
    public Page<EmcOrderItemBean> selectItemPagationByOrderItemGroup(@PathVariable Long orderItemGroupId, RestJsonWrapperBean wrapper) {
        return getService().selectItemPagationByOrderItemGroup(orderItemGroupId, wrapper);
    }

    @Override
    @Log(value = "批录入容器信息", type = LogType.INSERT)
    @RequestMapping(value = "/tasks/action/batch-update-container", method = RequestMethod.PUT)
    public void updateContainerInfo(RestJsonWrapperBean jsonWrapper) {
        getService().updateContainerInfo(jsonWrapper);
    }

    @Override
    @Log(value = "共同完成人批录入", type = LogType.INSERT)
    @RequestMapping(value = "/tasks/action/batch-update-partake", method = RequestMethod.POST)
    public void updatePartake(RestJsonWrapperBean jsonWrapper) {
        getService().updatePartake(jsonWrapper);
    }

    @Override
    @Log(value = "数据录入-结果初始化", type = LogType.SELECT)
    @RequestMapping(value = "{id}/action/init-task-results", method = RequestMethod.POST)
    public void initTaskResult(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        getService().initTaskResult(id, wrapper);
    }

    @Override
    @Log(value = "数据录入(分析录入)-置为完成(当前行)", type = LogType.UPDATE)
    @RequestMapping(value = "/results/action/set-done", method = RequestMethod.PUT)
    public void setDoneTaskResult(RestJsonWrapperBean wrapper) {
        getService().setDoneTaskResult(wrapper);
    }

    @Override
    @Log(value = "分析录入-置为完成(未录结果)", type = LogType.UPDATE)
    @RequestMapping(value = "/action/set-done", method = RequestMethod.PUT)
    public void setDoneOrder(RestJsonWrapperBean wrapper) {
        getService().setDoneOrder(wrapper);
    }

    @Override
    @Log(value = "数据录入-置为完成(未录结果)", type = LogType.UPDATE)
    @RequestMapping(value = "/folders/action/set-done", method = RequestMethod.PUT)
    public void setDoneSampleOrder(RestJsonWrapperBean wrapper) {
        getService().setDoneSampleOrder(wrapper);
    }

    @Override
    @Log(value = "数据录入-默认结果", type = LogType.UPDATE)
    @RequestMapping(value = "{id}/action/default-data-input-task-results", method = RequestMethod.POST)
    public void setDataInputDefaultTaskResult(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        getService().setDataInputDefaultTaskResult(id, wrapper);
    }

    @Override
    @Log(value = "数据录入-指派仪器", type = LogType.SELECT)
    @RequestMapping(value = "/order-tasks", method = RequestMethod.POST)
    public Page<EmcOrderTaskBean> selectOrderTaskEquipt(RestJsonWrapperBean wrapper) {
        return getService().selectOrderTaskEquipt(wrapper);
    }

    @Log(value = "通过样品查询容器信息", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/order-tasks/queries", method = RequestMethod.POST)
    public Page<EmcOrderTaskBean> selectOrderTaskPagationByOrder(RestJsonWrapperBean wrapper) {
        return getService().selectOrderTaskPagationByOrder(wrapper);
    }

    @Override
    @Log(value = "数据录入-添加重复数", type = LogType.INSERT)
    @RequestMapping(value = "{id}/action/add-repreat-nos", method = RequestMethod.POST)
    public void addRepeatNo(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        getService().addRepeatNo(id, wrapper);
    }

    @Override
    @Log(value = "数据录入-删除重复数", type = LogType.DELETE)
    @RequestMapping(value = "/action/delete-repreat-nos", method = RequestMethod.POST)
    public void deleteRepeatNo(RestJsonWrapperBean wrapper) {
        getService().deleteRepeatNo(wrapper);
    }

    @Override
    @Log(value = "数据录入-同步模板信息", type = LogType.UPDATE)
    @RequestMapping(value = "/action/sync-tmpl-infos", method = RequestMethod.POST)
    public void syncTmplInfo(RestJsonWrapperBean wrapper) {
        getService().syncTmplInfo(wrapper);
    }

    @Override
    @Log(value = "数据录入-去除公式", type = LogType.UPDATE)
    @RequestMapping(value = "/action/remove-fomulas", method = RequestMethod.POST)
    public void removeFomula(RestJsonWrapperBean wrapper) {
        getService().removeFomula(wrapper);
    }

    @Log(value = "设置分包商", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/item-groups/action/subcontractor-edit", method = RequestMethod.PUT)
    public void editSubcontractor(RestJsonWrapperBean wrapper) {
        getService().editSubcontractor(wrapper);
    }

    @Override
    @Log(value = "复制样品-现场采样", type = LogType.INSERT)
    @RequestMapping(value = "/action/copy-orders", method = RequestMethod.POST)
    public void copyOrders(RestJsonWrapperBean wrapper) {
        getService().copyOrders(wrapper);
    }

}
