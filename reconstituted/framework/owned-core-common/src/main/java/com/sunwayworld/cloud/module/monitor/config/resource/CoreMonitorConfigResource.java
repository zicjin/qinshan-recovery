/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.config.resource;

import com.sunwayworld.cloud.module.monitor.config.bean.CoreMonitorConfigBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/monitor/configs"})
public interface CoreMonitorConfigResource
extends GenericCloudResource<CoreMonitorConfigBean, String> {
}
