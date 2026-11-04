/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.config.service;

import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.sys.config.bean.CoreLoginConfigBean;

public interface CoreLoginConfigService
extends GenericService<CoreLoginConfigBean, String> {
    public CoreLoginConfigBean selectLoginConfig();
}
