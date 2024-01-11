package com.sunwayworld.emc.module.bp.bp.resource.impl;



import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderContainerBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderContainerService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import com.sunwayworld.emc.module.bp.bp.resource.PfOrderResource;
import com.sunwayworld.emc.module.bp.bp.resource.impl.EmcOrderResourceImpl;
import com.sunwayworld.emc.module.bp.bp.service.PfOrderService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.data.page.Page;


/**
 * @author licm@sunwayworld.com
 * @date 2021-11-09
 */
@LogModule("样品")
@RestController
@GikamBean
public class PfOrderResourceImpl extends EmcOrderResourceImpl implements PfOrderResource {

    @Autowired
    private PfOrderService orderService;

    @Autowired
    private EmcOrderContainerService orderContainerService;

    @Override
    public PfOrderService getService() {
        return orderService;
    }

    @Log(value = "更新扫描状态并删除标签表数据", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/update-scan-status", method = RequestMethod.POST)
    public void updateScanStatus(RestJsonWrapperBean wrapper) {
        getService().updateScanStatus(wrapper);
    }

    @Log(value = "退回样品至未扫描删除标签表数据", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/undo-scanned", method = RequestMethod.POST)
    public void undoScanned(RestJsonWrapperBean wrapper) {
        getService().undoScanned(wrapper);
    }

    @Override
    @Log(value = "项目方案修改-同步新增样品", type = LogType.UPDATE)
    @RequestMapping(value = "/action/sync-new-order", method = RequestMethod.POST)
    public void syncNewOrder(RestJsonWrapperBean wrapper) {
        getService().syncNewOrder(wrapper);
    }

    @Override
    @Log(value = "项目方案修改-同步新增测试", type = LogType.UPDATE)
    @RequestMapping(value = "/action/sync-new-test", method = RequestMethod.POST)
    public void syncNewTest(RestJsonWrapperBean wrapper) {
        getService().syncNewTest(wrapper);
    }

    @Override
    @Log(value = "项目方案修改-删除并同步样品", type = LogType.UPDATE)
    @RequestMapping(value = "/action/delete-sync-order", method = RequestMethod.POST)
    public void deleteSyncOrder(RestJsonWrapperBean wrapper) {
        getService().deleteSyncOrder(wrapper);
    }

    @Override
    @Log(value = "项目方案修改-删除并同步测试", type = LogType.UPDATE)
    @RequestMapping(value = "/action/delete-sync-test", method = RequestMethod.POST)
    public void deleteSyncTest(RestJsonWrapperBean wrapper) {
        getService().deleteSyncTest(wrapper);
    }

    @Override
    @Log(value = "分析数据修改-修复样品下重复测试", type = LogType.UPDATE)
    @RequestMapping(value = "/action/fix-duplicate-task", method = RequestMethod.POST)
    public void fixDuplicateOrderTask(RestJsonWrapperBean wrapper) {
        getService().fixDuplicateOrderTask(wrapper);
    }

    @Override
    @Log(value = "项目方案修改-分包测试", type = LogType.UPDATE)
    @RequestMapping(value = "/action/subpackage-test", method = RequestMethod.POST)
    public void subpackageTest(RestJsonWrapperBean wrapper) {
        getService().subpackageTest(wrapper);
    }

    @Override
    @Log(value = "项目方案修改-取消分包测试", type = LogType.UPDATE)
    @RequestMapping(value = "/action/cancel-subpackage-test", method = RequestMethod.POST)
    public void cancelSubpackageTest(RestJsonWrapperBean wrapper) {
        getService().cancelSubpackageTest(wrapper);
    }

    @Override
    @Log(value = "项目方案修改-删除并同步测试后操作", type = LogType.UPDATE)
    @RequestMapping(value = "/action/after-delete-test", method = RequestMethod.POST)
    public void afterDeleteTest(RestJsonWrapperBean wrapper) {
        getService().afterDeleteTest(wrapper);
    }

    @Override
    @Log(value = "项目方案修改-修改QC类型", type = LogType.UPDATE)
    @RequestMapping(value = "/action/edit-qc", method = RequestMethod.POST)
    public void editQc(RestJsonWrapperBean wrapper) {
        getService().editQc(wrapper);
    }

    @Override
    @Log(value = "项目方案修改-修改父样编号", type = LogType.UPDATE)
    @RequestMapping(value = "/action/edit-parentOrderNo", method = RequestMethod.POST)
    public void editParentOrderNo(RestJsonWrapperBean wrapper) {
        getService().editParentOrderNo(wrapper);
    }

    @Override
    @Log(value = "项目方案修改-重置父样编号", type = LogType.UPDATE)
    @RequestMapping(value = "/action/reset-parentOrderNo", method = RequestMethod.POST)
    public void resetParentOrderNo(RestJsonWrapperBean wrapper) {
        getService().resetParentOrderNo(wrapper);
    }

    @Log(value = "通过样品项目id查询分析项组", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/{id}/project/item-groups/queries", method = RequestMethod.POST)
    public Page<EmcOrderItemGroupBean> selectItemGroupPagationByProjId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().selectItemGroupPagationByProjId(id, wrapper);
    }

    @Log(value = "通过样品id查询分析项组", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/{id}/order/item-groups/queries", method = RequestMethod.POST)
    public Page<EmcOrderItemGroupBean> selectItemGroupPagationByOrderId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().selectItemGroupPagationByOrderId(id, wrapper);
    }


    @Override
    @Log(value = "分析录入-更改是否折算", type = LogType.UPDATE)
    @RequestMapping(value = "/action/change-is-convert", method = RequestMethod.POST)
    public void changeIsConvert(RestJsonWrapperBean wrapper) {
        getService().changeIsConvert(wrapper);
    }

    @Override
    @Log(value = "任务指派-更改ph标识", type = LogType.UPDATE)
    @RequestMapping(value = "/action/add-phflag", method = RequestMethod.POST)
    public void addPhFlag(RestJsonWrapperBean wrapper) {
        getService().addPhFlag(wrapper);
    }

    @Override
    @Log(value = "任务指派-删除ph标识", type = LogType.UPDATE)
    @RequestMapping(value = "/action/del-phflag", method = RequestMethod.POST)
    public void delPhFlag(RestJsonWrapperBean wrapper) {
        getService().delPhFlag(wrapper);
    }

    @Override
    @Log(value = "查询样品下的容器信息", type = LogType.SELECT)
    @RequestMapping(value = "/containers/queries", method = RequestMethod.POST)
    public Page<EmcOrderContainerBean> selectContainerPagination(RestJsonWrapperBean wrapper) {
        return getService().selectContainerPagination(wrapper);
    }

    @Override
    @Log(value = "样品领用查询容器", type = LogType.SELECT)
    @RequestMapping(value = "/receive/containers/queries", method = RequestMethod.POST)
    public Page<EmcOrderContainerBean> selectOrderReceiveContainerList(RestJsonWrapperBean wrapper) {
        return orderContainerService.selectOrderReceiveContainerList(wrapper);
    }

    @Override
    @Log(value = "现场采样-添加气采样参数", type = LogType.INSERT)
    @RequestMapping(value = "/action/add-param-order", method = RequestMethod.POST)
    public void addParamOrder(RestJsonWrapperBean wrapper) {
        getService().addParamOrder(wrapper);
    }

    @Override
    @Log(value = "现场采样-删除气采样参数", type = LogType.DELETE)
    @RequestMapping(value = "/action/delete-param-order", method = RequestMethod.POST)
    public void deleteParamOrder(RestJsonWrapperBean wrapper) {
        getService().deleteParamOrder(wrapper);
    }

    @Override
    @Log(value = "样品登记-更新评价指标", type = LogType.UPDATE)
    @RequestMapping(value = "/action/update-order-index", method = RequestMethod.POST)
    public void updateOrderIndex(RestJsonWrapperBean wrapper) {
        getService().updateOrderIndex(wrapper);
    }
}
