/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.log.resource;

import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.sys.log.bean.CoreExportLogBean;
import com.sunwayworld.module.sys.log.service.CoreExportLogService;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/sys/export-logs"})
public interface CoreExportLogResource
extends GenericResource<CoreExportLogService, CoreExportLogBean, Long> {
}
