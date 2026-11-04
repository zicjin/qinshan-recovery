/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.config.resource;

import com.sunwayworld.cloud.module.lcdp.config.bean.LcdpGlobalCompConfigBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/lcdp/global-comp-configs"})
public interface LcdpGlobalCompConfigResource
extends GenericCloudResource<LcdpGlobalCompConfigBean, Long> {
    @RequestMapping(method={RequestMethod.GET})
    public Map<String, List<LcdpGlobalCompConfigBean>> selectConfigList();
}
