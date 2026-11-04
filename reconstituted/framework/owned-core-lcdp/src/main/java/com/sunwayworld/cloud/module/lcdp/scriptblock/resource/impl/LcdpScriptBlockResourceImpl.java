/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.scriptblock.resource.impl;

import com.sunwayworld.cloud.module.lcdp.scriptblock.bean.LcdpScriptBlockBean;
import com.sunwayworld.cloud.module.lcdp.scriptblock.bean.LcdpScriptBlockTreeNodeDTO;
import com.sunwayworld.cloud.module.lcdp.scriptblock.resource.LcdpScriptBlockResource;
import com.sunwayworld.cloud.module.lcdp.scriptblock.service.LcdpScriptBlockService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u811a\u672c\u4ee3\u7801\u5757\u8868")
@RestController
@GikamBean
public class LcdpScriptBlockResourceImpl
implements LcdpScriptBlockResource,
AbstractGenericResource<LcdpScriptBlockService, LcdpScriptBlockBean, Long> {
    @Autowired
    private LcdpScriptBlockService lcdpScriptBlockService;

    @Override
    public LcdpScriptBlockService getService() {
        return this.lcdpScriptBlockService;
    }

    @Override
    @Log(value="\u65b0\u589e\u811a\u672c\u4ee3\u7801\u5757\u8868", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u4ee3\u7801\u5757\u6811", type=LogType.SELECT)
    public List<LcdpScriptBlockTreeNodeDTO> selectTree(RestJsonWrapperBean wrapper) {
        return this.getService().selectTree(wrapper);
    }

    @Override
    @Log(value="\u62d6\u62fd\u4ee3\u7801\u5757\u6811", type=LogType.SELECT)
    public void drag(RestJsonWrapperBean wrapper) {
        this.getService().drag(wrapper);
    }
}
