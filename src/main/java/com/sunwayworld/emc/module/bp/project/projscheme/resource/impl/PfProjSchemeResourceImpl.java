package com.sunwayworld.emc.module.bp.project.projscheme.resource.impl;

import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjOrderBean;
import com.sunwayworld.framework.data.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.project.projscheme.resource.PfProjSchemeResource;
import com.sunwayworld.emc.module.bp.project.projscheme.service.PfProjSchemeService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule("监测方案表")
@RestController
@GikamBean
public class PfProjSchemeResourceImpl extends EmcProjSchemeResourceImpl implements PfProjSchemeResource {

    @Autowired
    private PfProjSchemeService pfprojSchemeService;

    @Override
    public PfProjSchemeService getService() {
        return pfprojSchemeService;
    }

    @Log(value = "更新监测点名称同步folder表", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/action/sync-folder", method = RequestMethod.POST)
    public void syncFolder(RestJsonWrapperBean wrapper) {
        getService().syncFolder(wrapper);
    }

    @Override
    @Log(value = "项目方案修改-修改方案信息", type = LogType.UPDATE)
    @RequestMapping(value = "/action/update-scheme", method = RequestMethod.POST)
    public void updateScheme(RestJsonWrapperBean wrapper) {
        getService().updateScheme(wrapper);
    }

    @Override
    @Log(value = "项目方案修改-修改测试信息", type = LogType.UPDATE)
    @RequestMapping(value = "/action/update-scheme-order", method = RequestMethod.POST)
    public void updateSchemeOrder(RestJsonWrapperBean wrapper) {
        getService().updateSchemeOrder(wrapper);
    }

    @Override
    @Log(value = "项目方案修改-同步新增点位", type = LogType.UPDATE)
    @RequestMapping(value = "/action/sync-new-folder", method = RequestMethod.POST)
    public void syncNewFolder(RestJsonWrapperBean wrapper) {
        getService().syncNewFolder(wrapper);
    }

    @Override
    @Log(value = "项目方案修改-同步新增测试", type = LogType.UPDATE)
    @RequestMapping(value = "/action/sync-new-test", method = RequestMethod.POST)
    public void syncNewTest(RestJsonWrapperBean wrapper) {
        getService().syncNewTest(wrapper);
    }

    @Override
    @Log(value = "项目方案修改-删除点位", type = LogType.UPDATE)
    @RequestMapping(value = "/action/delete-folder", method = RequestMethod.POST)
    public void deleteFolder(RestJsonWrapperBean wrapper) {
        getService().deleteFolder(wrapper);
    }

    @Override
    @Log(value = "项目方案修改-从方案同步点位", type = LogType.UPDATE)
    @RequestMapping(value = "/action/sync-folder-from-scheme", method = RequestMethod.POST)
    public void syncFolderFromScheme(RestJsonWrapperBean wrapper) {
        getService().syncFolderFromScheme(wrapper);
    }

    @Override
    @Log(value = "项目方案修改-删除样品", type = LogType.UPDATE)
    @RequestMapping(value = "/action/delete-order", method = RequestMethod.POST)
    public void deleteOrder(RestJsonWrapperBean wrapper) {
        getService().deleteOrder(wrapper);
    }

    @Override
    @Log(value = "项目方案修改-从方案同步样品", type = LogType.UPDATE)
    @RequestMapping(value = "/action/sync-order-from-scheme", method = RequestMethod.POST)
    public void syncOrderFromScheme(RestJsonWrapperBean wrapper) {
        getService().syncOrderFromScheme(wrapper);
    }

    @Override
    @Log(value = "项目方案修改-复制样品", type = LogType.UPDATE)
    @RequestMapping(value = "/action/copy-order", method = RequestMethod.POST)
    public void copyOrder(RestJsonWrapperBean wrapper) {
        getService().copyOrder(wrapper);
    }

    @Override
    @Log(value = "项目方案修改-删除测试", type = LogType.UPDATE)
    @RequestMapping(value = "/action/delete-test", method = RequestMethod.POST)
    public void deleteTest(RestJsonWrapperBean wrapper) {
        getService().deleteTest(wrapper);
    }

    @Override
    @Log(value = "项目方案修改-从方案同步测试", type = LogType.UPDATE)
    @RequestMapping(value = "/action/sync-test-from-scheme", method = RequestMethod.POST)
    public void syncTestFromScheme(RestJsonWrapperBean wrapper) {
        getService().syncTestFromScheme(wrapper);
    }

    @Override
    @Log(value = "完成点位", type = LogType.INSERT)
    @RequestMapping(value = "/action/complete-folder", method = RequestMethod.POST)
    public void completeFolder(RestJsonWrapperBean wrapper) {
        getService().completeFolder(wrapper);
    }

    @Override
    @Log(value = "完成点位按样品", type = LogType.INSERT)
    @RequestMapping(value = "/action/complete-folder-for-order", method = RequestMethod.POST)
    public void completeFolderForOrder(RestJsonWrapperBean wrapper) {
        getService().completeFolderForOrder(wrapper);
    }

    @Override
    @Log(value = "取消完成点位", type = LogType.INSERT)
    @RequestMapping(value = "/action/cancel-folder", method = RequestMethod.POST)
    public void cancelFolder(RestJsonWrapperBean wrapper) {
        getService().cancelFolder(wrapper);
    }

    @Override
    @Log(value = "取消完成点位按样品", type = LogType.INSERT)
    @RequestMapping(value = "/action/cancel-folder-for-order", method = RequestMethod.POST)
    public void cancelFolderForOrder(RestJsonWrapperBean wrapper) {
        getService().cancelFolderForOrder(wrapper);
    }

    @Override
    @Log(value = "从样品移除测试", type = LogType.INSERT)
    @RequestMapping(value = "/action/remove-order-task", method = RequestMethod.POST)
    public String removeOrderTask(RestJsonWrapperBean wrapper) {
        return getService().removeOrderTask(wrapper);
    }

    @Log(value = "通过监测方案项目id查询样品", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/{id}/proj-orders/project/queries", method = RequestMethod.POST)
    public Page<EmcProjOrderBean> selectProjOrderProjectPagationByProjScheme(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return pfprojSchemeService.selectProjOrderProjectPagationByProjScheme(id, wrapper);
    }

    @Log(value = "通过监测方案样品id查询测试", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/{id}/proj-orders/order/queries", method = RequestMethod.POST)
    public Page<EmcProjOrderBean> selectProjOrderOrderPagationByProjScheme(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return pfprojSchemeService.selectProjOrderOrderPagationByProjScheme(id, wrapper);
    }

    @Override
    @Log(value = "监测方案上下移动", type = LogType.UPDATE)
    @RequestMapping(value = "/move", method = RequestMethod.PUT)
    public void move(RestJsonWrapperBean wrapper) {
        pfprojSchemeService.move(wrapper);
    }

    @Override
    @Log(value = "监测方案样品上下移动", type = LogType.UPDATE)
    @RequestMapping(value = "/move-proj-order", method = RequestMethod.PUT)
    public void moveProjOrder(RestJsonWrapperBean wrapper) {
        pfprojSchemeService.moveProjOrder(wrapper);
    }
    @Override
    @Log(value = "删除方案样品", type = LogType.UPDATE)
    @RequestMapping(value = "/proj-order", method = RequestMethod.DELETE)
    public void delProjOrder(RestJsonWrapperBean wrapper) {
        getService().delProjOrder(wrapper);
    }

    @Override
    @Log(value = "方案点位编号排序", type = LogType.UPDATE)
    @RequestMapping(value = "/sort-folderno", method = RequestMethod.PUT)
    public void sortFolderNo(RestJsonWrapperBean wrapper) {
        getService().sortFolderNo(wrapper);
    }

    @Override
    @Log(value = "批量删除方案样品", type = LogType.UPDATE)
    @RequestMapping(value = "/action/batch-del-proj-order", method = RequestMethod.POST)
    public Long batchDelProjOrder(RestJsonWrapperBean wrapper) {
        getService().batchDelProjOrder(wrapper);
        return 1L;
    }

    @Override
    @Log(value = "同步评价标准", type = LogType.UPDATE)
    @RequestMapping(value = "/action/sync-eval-std", method = RequestMethod.POST)
    public void syncEvalStd(RestJsonWrapperBean wrapper) {
        getService().syncEvalStd(wrapper);
    }

}
