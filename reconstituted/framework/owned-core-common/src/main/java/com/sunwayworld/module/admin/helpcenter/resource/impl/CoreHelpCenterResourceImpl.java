/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.helpcenter.resource.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.admin.helpcenter.bean.CoreHelpCenterDTO;
import com.sunwayworld.module.admin.helpcenter.resource.CoreHelpCenterResource;
import com.sunwayworld.module.admin.helpcenter.service.CoreHelpCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u5e2e\u52a9\u4e2d\u5fc3")
@RestController
@GikamBean
public class CoreHelpCenterResourceImpl
implements CoreHelpCenterResource {
    @Autowired
    private CoreHelpCenterService coreHelpCenterService;

    @Override
    public CoreHelpCenterService getService() {
        return this.coreHelpCenterService;
    }

    @Override
    @Log(value="\u65b0\u589e\u5e2e\u52a9\u4e2d\u5fc3", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public String insert(RestJsonWrapperBean wrapper) {
        return (String)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u83dc\u5355\u7f16\u7801\u67e5\u8be2\u5e2e\u52a9\u4e2d\u5fc3", type=LogType.SELECT)
    @RequestMapping(value={"/menus/{menuId}"}, method={RequestMethod.GET})
    public CoreHelpCenterDTO selectHelpCenterDTOByMenuId(String menuId) {
        return this.getService().selectHelpCenterDTOByMenuId(menuId);
    }
}
