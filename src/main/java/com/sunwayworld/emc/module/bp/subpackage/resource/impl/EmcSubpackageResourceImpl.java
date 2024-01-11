package com.sunwayworld.emc.module.bp.subpackage.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.subpackage.bean.EmcSubpackageTaskBean;
import com.sunwayworld.emc.module.bp.subpackage.resource.EmcSubpackageResource;
import com.sunwayworld.emc.module.bp.subpackage.service.EmcSubpackageService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
* @author zhengxy@sunwayworld.com 2022年09月05日
*/
@GikamBean
@RestController
@LogModule(value = "分包单测试")
public class EmcSubpackageResourceImpl implements EmcSubpackageResource {

    @Autowired
    private EmcSubpackageService emcSubpackageService;

    @Override
    public EmcSubpackageService getService() {
        return emcSubpackageService;
    }

    @Override
    @Log(value = "创建分包单", type = LogType.INSERT)
    @RequestMapping(value = "/action/create", method = RequestMethod.POST)
    public void createPackage(RestJsonWrapperBean wrapper) {
        emcSubpackageService.createPackage(wrapper);
    }

    @Override
    @Log(value = "添加至分包单", type = LogType.INSERT)
    @RequestMapping(value = "/action/add-to", method = RequestMethod.POST)
    public void addToPackage(RestJsonWrapperBean wrapper) {
        emcSubpackageService.addToPackage(wrapper);
    }

    @Override
    @Log(value = "委派分包单", type = LogType.INSERT)
    @RequestMapping(value = "/action/assign", method = RequestMethod.POST)
    public void assignPackage(RestJsonWrapperBean wrapper) {
        emcSubpackageService.assignPackage(wrapper);
    }

    @Override
    @Log(value = "创建合同", type = LogType.INSERT)
    @RequestMapping(value = "/action/create-contract", method = RequestMethod.POST)
    public void createContract(RestJsonWrapperBean wrapper) {
        emcSubpackageService.createContract(wrapper);
    }

    @Override
    @Log(value = "查询分包单测试", type = LogType.SELECT)
    @RequestMapping(value = "/tasks/queries", method = RequestMethod.POST)
    public Page<EmcSubpackageTaskBean> queryTasks(RestJsonWrapperBean wrapper) {
        return emcSubpackageService.queryTasks(wrapper);
    }

    @Override
    @Log(value = "创建项目分包单", type = LogType.INSERT)
    @RequestMapping(value = "/action/create-proj-package", method = RequestMethod.POST)
    public void createProjPackage(RestJsonWrapperBean wrapper) {
        emcSubpackageService.createProjPackage(wrapper);
    }

    @Override
    @Log(value = "撤销分包单", type = LogType.DELETE)
    @RequestMapping(value = "/action/cancel-package", method = RequestMethod.POST)
    public String cancelPackage(RestJsonWrapperBean wrapper) {
        return  emcSubpackageService.cancelPackage(wrapper);
    }

    @Override
    @Log(value = "删除分包测试", type = LogType.DELETE)
    @RequestMapping(value = "/action/delete-package-task", method = RequestMethod.POST)
    public void deletePackageTask(RestJsonWrapperBean wrapper) {
          emcSubpackageService.deletePackageTask(wrapper);
    }

    @Override
    @Log(value = "完成分包", type = LogType.UPDATE)
    @RequestMapping(value = "/action/complete-package", method = RequestMethod.POST)
    public void completePackage(RestJsonWrapperBean wrapper) {
        emcSubpackageService.completePackage(wrapper);
    }

    @Override
    @Log(value = "提交到分包委托", type = LogType.UPDATE)
    @RequestMapping(value = "/action/submit-to-subpackage", method = RequestMethod.POST)
    public void submitToSubpackage(RestJsonWrapperBean wrapper) {
        emcSubpackageService.submitToSubpackage(wrapper);
    }

    @Override
    @Log(value = "分包委托退回", type = LogType.UPDATE)
    @RequestMapping(value = "/action/undo-subpackage", method = RequestMethod.POST)
    public void undoSubpackage(RestJsonWrapperBean wrapper) {
        emcSubpackageService.undoSubpackage(wrapper);
    }


}
