/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.resource.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.menu.resource.CoreShortcutMenuLocationResource;
import com.sunwayworld.module.sys.menu.service.CoreShortcutMenuLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u5feb\u6377\u76ee\u5f55\u5c55\u793a\u4f4d\u7f6e\u8868")
@RestController
@GikamBean
public class CoreShortcutMenuLocationResourceImpl
implements CoreShortcutMenuLocationResource {
    @Autowired
    private CoreShortcutMenuLocationService coreShortcutMenuLocationService;

    @Override
    public CoreShortcutMenuLocationService getService() {
        return this.coreShortcutMenuLocationService;
    }

    @Override
    @Log(value="\u4fee\u6539\u5feb\u6377\u76ee\u5f55\u5c55\u793a\u4f4d\u7f6e", type=LogType.UPDATE)
    @RequestMapping(value={"/modifyLocation/{location}"}, method={RequestMethod.POST})
    public void modifyLocation(@PathVariable(value="location") Long location) {
        this.getService().modifyLocation(location);
    }
}
