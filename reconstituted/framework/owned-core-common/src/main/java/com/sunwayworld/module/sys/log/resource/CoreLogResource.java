/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.log.resource;

import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.sys.log.bean.CoreLogBean;
import com.sunwayworld.module.sys.log.service.CoreLogService;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/core/module/sys/logs"})
public interface CoreLogResource
extends GenericResource<CoreLogService, CoreLogBean, Long> {
}
