/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.config.service;

import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.sys.config.bean.CoreSystemConfigBean;
import com.sunwayworld.module.sys.config.bean.CoreSystemConfigDTO;

public interface CoreSystemConfigService
extends GenericService<CoreSystemConfigBean, String> {
    public CoreSystemConfigDTO selectSystemConfig();
}
