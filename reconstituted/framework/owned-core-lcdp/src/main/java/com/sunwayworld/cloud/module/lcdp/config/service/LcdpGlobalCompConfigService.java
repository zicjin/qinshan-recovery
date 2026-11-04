/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.config.service;

import com.sunwayworld.cloud.module.lcdp.config.bean.LcdpGlobalCompConfigBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import java.util.List;
import java.util.Map;

public interface LcdpGlobalCompConfigService
extends GenericService<LcdpGlobalCompConfigBean, Long> {
    public Map<String, List<LcdpGlobalCompConfigBean>> selectConfigList();
}
