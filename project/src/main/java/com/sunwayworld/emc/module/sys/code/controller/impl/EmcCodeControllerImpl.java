package com.sunwayworld.emc.module.sys.code.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.code.controller.impl.CoreCodeControllerImpl;

/**
 * @author zhengxy@sunwayworld.com 2023年12月15日
 */
@GikamBean
@Controller
public class EmcCodeControllerImpl extends CoreCodeControllerImpl {

    @Override
    public String editListPage() {
        return "emc/module/sys/codes/emc-core-code-edit-list";
    }
}
