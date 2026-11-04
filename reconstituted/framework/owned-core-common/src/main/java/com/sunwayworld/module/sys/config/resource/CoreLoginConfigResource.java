/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.config.resource;

import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.sys.config.bean.CoreLoginConfigBean;
import com.sunwayworld.module.sys.config.service.CoreLoginConfigService;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/module/sys/login-configs"})
public interface CoreLoginConfigResource
extends GenericResource<CoreLoginConfigService, CoreLoginConfigBean, String> {
}
