// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.bp.bp.controller.impl;

import com.sunwayworld.emc.module.bp.bp.controller.EmcSampleSetController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class EmcSampleSetControllerImpl
implements EmcSampleSetController {
    @Override
    @Log(value="\u91c7\u6837\u57fa\u51c6\u542b\u6c27\u914d\u7f6e\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/bp/sample-sets/emc-sample-set-edit-list";
    }

    @Override
    @Log(value="\u91c7\u6837\u57fa\u51c6\u542b\u6c27\u914d\u7f6e\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String detailPage() {
        return "emc/module/bp/sample-sets/emc-sample-set-detail";
    }

    @Override
    @Log(value="\u91c7\u6837\u57fa\u51c6\u542b\u6c27\u914d\u7f6e\u67e5\u8be2\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String searchListPage() {
        return "emc/module/bp/sample-sets/emc-sample-set-search-list";
    }

    @Override
    @Log(value="\u91c7\u6837\u57fa\u51c6\u542b\u6c27\u914d\u7f6e\u9009\u62e9\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String choosePage() {
        return "emc/module/bp/sample-sets/emc-sample-set-choose-list";
    }
}
