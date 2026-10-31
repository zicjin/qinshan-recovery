package com.sunwayworld.emc.module.bp.bp.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.resource.PfOrderTaskResource;
import com.sunwayworld.emc.module.bp.bp.service.PfOrderTaskService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2021年11月12日
 */
@GikamBean
@RestController
public class PfOrderTaskResourceImpl extends EmcOrderTaskResourceImpl implements PfOrderTaskResource {

    @Autowired
    private PfOrderTaskService orderTaskService;

    @Override
    @Log(value = "更新称重日期", type = LogType.UPDATE)
    @RequestMapping(value = "/action/update-weighing-date", method = RequestMethod.POST)
    public void updateWeighingDate(RestJsonWrapperBean wrapper) {
        orderTaskService.updateWeighingDate(wrapper);
    }

    @Override
    @Log(value = "更新称重结束日期", type = LogType.UPDATE)
    @RequestMapping(value = "/action/update-weighing-end-date", method = RequestMethod.POST)
    public void updateWeighingEndDate(RestJsonWrapperBean wrapper) {
        orderTaskService.updateWeighingEndDate(wrapper);
    }

    @Override
    @Log(value = "更新VOC瓶号", type = LogType.UPDATE)
    @RequestMapping(value = "/action/update-voc-bottle", method = RequestMethod.POST)
    public void updateVocBottle(RestJsonWrapperBean wrapper) {
        orderTaskService.updateVocBottle(wrapper);
    }

    @Override
    @Log(value = "同步voc瓶号", type = LogType.UPDATE)
    @RequestMapping(value = "/action/sync-voc-bottle", method = RequestMethod.POST)
    public void batchAutoUpdateVocBottle(RestJsonWrapperBean wrapper) {
        orderTaskService.batchAutoUpdateVocBottle(wrapper);
    }

    @Override
    @Log(value = "更新标准物质批号", type = LogType.UPDATE)
    @RequestMapping(value = "/action/update-batch-no", method = RequestMethod.POST)
    public void updateBatchNo(RestJsonWrapperBean wrapper) {
        orderTaskService.updateBatchNo(wrapper);
    }

    @Override
    @Log(value = "单样品所有测试批录入", type = LogType.UPDATE)
    @RequestMapping(value = "/action/batch-update-for-order", method = RequestMethod.POST)
    public void batchUpdateForOrder(RestJsonWrapperBean wrapper) {
        orderTaskService.batchUpdateForOrder(wrapper);
    }

    @Override
    @Log(value = "查询待添加批的样品", type = LogType.SELECT)
    @RequestMapping(value = "/unique-orders/queries", method = RequestMethod.POST)
    public Page<EmcOrderTaskBean> selectUniqueOrderPagination(RestJsonWrapperBean wrapper) {
        return orderTaskService.selectUniqueOrderList(wrapper);
    }

    @Override
    @Log(value = "查询样品测试分析项组", type = LogType.SELECT)
    @RequestMapping(value = "/unique-item-groups/queries", method = RequestMethod.POST)
    public Page<EmcOrderTaskBean> selectUniqueItemGroupPagination(RestJsonWrapperBean wrapper) {
        return orderTaskService.selectUniqueItemGroupPagination(wrapper);
    }

    @Override
    @Log(value = "测试批录入", type = LogType.UPDATE)
    @RequestMapping(value = "/action/batch-update-order-task", method = RequestMethod.POST)
    public void batchUpdateOrderTask(RestJsonWrapperBean wrapper) {
        orderTaskService.batchUpdateOrderTask(wrapper);
    }

    @Override
    @Log(value = "筛选库存编号", type = LogType.UPDATE)
    @RequestMapping(value = "/action/select-stock-by-order-task", method = RequestMethod.POST)
    public String selectStockNoByOrderTask(RestJsonWrapperBean wrapper) {
        return orderTaskService.selectStockNoByOrderTask(wrapper);
    }

    @Override
    @Log(value = "筛选库存测试", type = LogType.UPDATE)
    @RequestMapping(value = "/action/select-match-stock-testmethodId", method = RequestMethod.POST)
    public String selectMatchStockTestMethodId(RestJsonWrapperBean wrapper) {
        return orderTaskService.selectMatchStockTestMethodId(wrapper);
    }

    @Override
    @Log(value = "需删除的测试名称", type = LogType.UPDATE)
    @RequestMapping(value = "/action/select-over-test", method = RequestMethod.POST)
    public String selectOverTest(RestJsonWrapperBean wrapper) {
        return orderTaskService.selectOverTest(wrapper);
    }

    @Override
    @Log(value = "更新滤筒编号", type = LogType.UPDATE)
    @RequestMapping(value = "/action/update-oil-filmno", method = RequestMethod.POST)
    public void updateOilFilmNo(RestJsonWrapperBean wrapper) {
        orderTaskService.updateOilFilmNo(wrapper);
    }

    @Override
    @Log(value = "同步滤筒瓶号", type = LogType.UPDATE)
    @RequestMapping(value = "/action/sync-oil-filmno", method = RequestMethod.POST)
    public void syncOilFilmno(RestJsonWrapperBean wrapper) {
        orderTaskService.syncOilFilmno(wrapper);
    }

    @Override
    @Log(value = "有机同步仪器id", type = LogType.UPDATE)
    @RequestMapping(value = "/action/sync-no-equiptid-task", method = RequestMethod.POST)
    public void syncNoEquiptIdTask(RestJsonWrapperBean wrapper) {
        orderTaskService.syncNoEquiptIdTask(wrapper);
    }

    @Override
    @Log(value = "项目方案修改-分包测试", type = LogType.UPDATE)
    @RequestMapping(value = "/action/subpackage", method = RequestMethod.POST)
    public void subpackageTest(RestJsonWrapperBean wrapper) {
        orderTaskService.subpackageTest(wrapper);
    }

    @Override
    @Log(value = "项目方案修改-取消分包测试", type = LogType.UPDATE)
    @RequestMapping(value = "/action/cancel-subpackage", method = RequestMethod.POST)
    public void cancelSubpackageTest(RestJsonWrapperBean wrapper) {
        orderTaskService.cancelSubpackageTest(wrapper);
    }

    @Override
    @Log(value = "更新VOC瓶号", type = LogType.UPDATE)
    @RequestMapping(value = "/action/update-sample-bottle", method = RequestMethod.POST)
    public void updateSampleBottle(RestJsonWrapperBean wrapper) {
        orderTaskService.updateSampleBottle(wrapper);
    }

    @Override
    @Log(value = "同步voc瓶号", type = LogType.UPDATE)
    @RequestMapping(value = "/action/sync-sample-bottle", method = RequestMethod.POST)
    public void batchAutoUpdateSampleBottle(RestJsonWrapperBean wrapper) {
        orderTaskService.batchAutoUpdateSampleBottle(wrapper);
    }

    @Override
    @Log(value = "查询分瓶信息", type = LogType.SELECT)
    @RequestMapping(value = "/queries/selectContainerInfo", method = RequestMethod.POST)
    public Page<EmcOrderTaskBean> selectContainerInfo(RestJsonWrapperBean wrapper) {
        return orderTaskService.selectContainerInfo(wrapper);
    }

    @Override
    @Log(value = "采样单获取测试类型", type = LogType.UPDATE)
    @RequestMapping(value = "/queries/group-by-test", method = RequestMethod.POST)
    public Page<EmcOrderTaskBean> selectTaskGroupByTest(RestJsonWrapperBean wrapper) {
        return orderTaskService.selectTaskGroupByTest(wrapper);
    }

    @Override
    @Log(value = "分析录入-质控样", type = LogType.SELECT)
    @RequestMapping(value = "/queries/run-qc/{runId}", method = RequestMethod.POST)
    public Page<EmcOrderTaskBean> selectByRunQc(@PathVariable String runId, RestJsonWrapperBean wrapper) {
        return orderTaskService.selectByRunQc(runId, wrapper);
    }

    @Override
    @Log(value = "查询标签信息", type = LogType.SELECT)
    @RequestMapping(value = "/queries/select-order-container-info", method = RequestMethod.POST)
    public Page<EmcOrderTaskBean> selectOrderContainerInfo(RestJsonWrapperBean wrapper) {
        return orderTaskService.selectOrderContainerInfo(wrapper);
    }

    @Override
    @Log(value = "查询标签信息", type = LogType.SELECT)
    @RequestMapping(value = "/queries/order-task", method = RequestMethod.POST)
    public Page<EmcOrderTaskBean> selectOrderTaskPagination(RestJsonWrapperBean wrapper) {
        return orderTaskService.selectOrderTaskPagination(wrapper);
    }

}
