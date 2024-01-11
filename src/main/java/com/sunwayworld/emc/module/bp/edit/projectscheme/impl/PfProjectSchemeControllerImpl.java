package com.sunwayworld.emc.module.bp.edit.projectscheme.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.bp.edit.projectscheme.PfProjectSchemeController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2021年11月24日
 */
@Controller
@GikamBean
public class PfProjectSchemeControllerImpl implements PfProjectSchemeController {

    @Override
    @Log(value = "项目方案修改列表页", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/bp/edit/project-schemes/emc-project-scheme-edit-list";
    }

    @Override
    @Log(value = "项目方案修改-选择监测方案", type = LogType.CONTROLLER)
    public String chooseSchemePage() {
        return "emc/module/bp/edit/project-schemes/emc-project-scheme-choose-list";
    }

    @Override
    @Log(value = "项目方案修改-选择监测方案测试", type = LogType.CONTROLLER)
    public String chooseSchemeTestPage() {
        return "emc/module/bp/edit/project-schemes/emc-project-scheme-test-choose-list";
    }
}
