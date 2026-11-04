/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.printer.printlog.resource;

import com.sunwayworld.cloud.module.printer.printlog.bean.CorePrintLogBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/printer/print-logs"})
public interface CorePrintLogResource
extends GenericCloudResource<CorePrintLogBean, Long> {
}
