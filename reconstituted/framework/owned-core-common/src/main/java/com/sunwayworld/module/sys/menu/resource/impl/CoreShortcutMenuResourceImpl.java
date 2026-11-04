/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.resource.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.menu.bean.CoreShortcutMenuBean;
import com.sunwayworld.module.sys.menu.resource.CoreShortcutMenuResource;
import com.sunwayworld.module.sys.menu.service.CoreShortcutMenuService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u5feb\u6377\u83dc\u5355")
@RestController
@GikamBean
public class CoreShortcutMenuResourceImpl
implements CoreShortcutMenuResource {
    @Autowired
    private CoreShortcutMenuService coreShortcutMenuService;

    @Override
    public CoreShortcutMenuService getService() {
        return this.coreShortcutMenuService;
    }

    @Override
    @Log(value="\u65b0\u589e\u5feb\u6377\u83dc\u5355", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u5feb\u6377\u83dc\u5355", type=LogType.SELECT)
    @RequestMapping(value={"/queries/accessable"}, method={RequestMethod.POST}, produces={"application/json"})
    @ResponseBody
    public List<CoreShortcutMenuBean> selectCurrentUserAccessableList() {
        return this.getService().selectCurrentUserAccessableList();
    }
}
