/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.config.resource;

import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.sys.config.bean.CoreSystemConfigBean;
import com.sunwayworld.module.sys.config.service.CoreSystemConfigService;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/module/sys/system-configs"})
public interface CoreSystemConfigResource
extends GenericResource<CoreSystemConfigService, CoreSystemConfigBean, String> {
}
