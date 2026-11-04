/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.config.resource.impl;

import com.sunwayworld.cloud.module.lcdp.config.bean.LcdpGlobalCompConfigBean;
import com.sunwayworld.cloud.module.lcdp.config.resource.LcdpGlobalCompConfigResource;
import com.sunwayworld.cloud.module.lcdp.config.service.LcdpGlobalCompConfigService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u4f4e\u4ee3\u7801\u7cfb\u7edf\u5168\u5c40\u7ec4\u4ef6\u914d\u7f6e")
@RestController
@GikamBean
public class LcdpGlobalCompConfigResourceImpl
implements LcdpGlobalCompConfigResource,
AbstractGenericResource<LcdpGlobalCompConfigService, LcdpGlobalCompConfigBean, Long> {
    @Autowired
    private LcdpGlobalCompConfigService lcdpGlobalCompConfigService;

    @Override
    public LcdpGlobalCompConfigService getService() {
        return this.lcdpGlobalCompConfigService;
    }

    @Override
    @Log(value="\u65b0\u589e\u4f4e\u4ee3\u7801\u7cfb\u7edf\u5168\u5c40\u7ec4\u4ef6\u914d\u7f6e", type=LogType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u4f4e\u4ee3\u7801\u7cfb\u7edf\u5168\u5c40\u7ec4\u4ef6\u914d\u7f6e", type=LogType.SELECT)
    public Map<String, List<LcdpGlobalCompConfigBean>> selectConfigList() {
        return this.getService().selectConfigList();
    }
}
