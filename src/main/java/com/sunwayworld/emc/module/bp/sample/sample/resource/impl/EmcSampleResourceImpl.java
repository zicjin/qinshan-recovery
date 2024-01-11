package com.sunwayworld.emc.module.bp.sample.sample.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.emc.module.bp.sample.sample.resource.EmcSampleResource;
import com.sunwayworld.emc.module.bp.sample.sample.service.EmcSampleService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */

@LogModule("采样单")
@RestController
@GikamBean
public class EmcSampleResourceImpl implements EmcSampleResource {

    @Autowired
    private EmcSampleService emcSampleService;

    @Override
    public EmcSampleService getService() {
        return emcSampleService;
    }

    @Log(value = "新增采样单", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

    @Override
    @Log(value = "添加样品", type = LogType.INSERT)
    @RequestMapping(value = "/{id}/orders", method = RequestMethod.POST)
    public void addOrder(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        getService().addOrder(id, wrapper);
    }

    @Override
    @Log(value = "修改样品", type = LogType.UPDATE)
    @RequestMapping(value = "/{id}/orders/sample-date-time", method = RequestMethod.PUT)
    public void updateOrderSampleDateTime(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        getService().updateOrderSampleDateTime(id, wrapper);
    }

    @Override
    @Log(value = "删除样品", type = LogType.DELETE)
    @RequestMapping(value = "/orders", method = RequestMethod.DELETE)
    public void deleteOrder(RestJsonWrapperBean wrapper) {
        getService().deleteOrder(wrapper);
    }

    @Log(value = "报出结果添加", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/action/change-result", method = RequestMethod.PUT)
    public void changeResult(RestJsonWrapperBean wrapper) {
        getService().changeResult(wrapper);
    }

    @Override
    @Log(value = "合并采样单", type = LogType.INSERT)
    @RequestMapping(value = "/action/merge-samples", method = RequestMethod.POST)
    public void mergeSample( RestJsonWrapperBean wrapper) {
        getService().mergeSample(wrapper);
    }

    @Log(value = "样品接收采样单接收", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/action/accept", method = RequestMethod.PUT)
    public void accept(RestJsonWrapperBean wrapper) {
        getService().accept(wrapper);
    }

    @Log(value = "选择分析项", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/select-item/{id}", method = RequestMethod.POST)
    public Page<EmcItemBean> selectEditItem(@PathVariable Long id) {
        return getService().selectEditItem(id);
    }

    @Log(value = "编辑分析项", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/edit-item", method = RequestMethod.POST)
    public Long editItem(RestJsonWrapperBean wrapper) {
        return getService().editItem(wrapper);
    }

    @Override
    @Log(value = "工作流启动", type = LogType.PROCESS)
    @RequestMapping(value = "/action/start-process", method = RequestMethod.PUT)
    public void startProcess(RestJsonWrapperBean wrapper) {
        getService().startProcess(wrapper);
    }

    @Override
    @Log(value = "查询所有状态的批数据", type = LogType.UPDATE)
    @RequestMapping(value = "/queries/all", method = RequestMethod.POST)
    public Page<EmcSampleBean> selectAllPagination(RestJsonWrapperBean wrapper) {
        return getService().selectAllPagination(wrapper);
    }

    @Override
    @Log(value = "通过采样单ID查询样品测试", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/order-tasks/queries", method = RequestMethod.POST)
    public Page<EmcOrderTaskBean> selectOrderTaskBySampleId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().selectOrderTaskBySampleId(id, wrapper);
    }

    @Override
    @Log(value = "按样品测试批录入样品测试结果", type = LogType.UPDATE)
    @RequestMapping(value = "/order-tasks/action/batch-input", method = RequestMethod.POST)
    public void batchInputOrderTaskResult(RestJsonWrapperBean wrapper) {
        getService().batchInputOrderTaskResult(wrapper);
    }

    @Override
    @Log(value = "通过采样单查询样品测试", type = LogType.SELECT)
    @RequestMapping(value = "/order-tasks/queries", method = RequestMethod.POST)
    public Page<EmcOrderTaskBean> selectOrderTaskBySample(RestJsonWrapperBean wrapper) {
        return getService().selectOrderTaskBySample(wrapper);
    }

    @Override
    @Log(value = "回退--样品接收页面", type = LogType.UPDATE)
    @RequestMapping(value = "/action/bizUndo", method = RequestMethod.PUT)
    public void bizUndo(RestJsonWrapperBean wrapper) {
        getService().bizUndo(wrapper);
    }

    @Override
    @Log(value = "现场采样提交到结果录入", type = LogType.UPDATE)
    @RequestMapping(value = "/result-input", method = RequestMethod.POST)
    public Long resultInput(RestJsonWrapperBean wrapper) {
        return getService().resultInput(wrapper);
    }

    @Override
    @Log(value = "已接收的采样单退回", type = LogType.UPDATE)
    @RequestMapping(value = "/action/accepted-undo", method = RequestMethod.POST)
    public void acceptedUndo(RestJsonWrapperBean wrapper) {
        getService().acceptedUndo(wrapper);
    }
}
