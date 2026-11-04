/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.printer.printlog.service;

import com.sunwayworld.cloud.module.printer.printer.bean.CorePrinterPropertiesDTO;
import com.sunwayworld.cloud.module.printer.printlog.bean.CorePrintLogBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface CorePrintLogService
extends GenericService<CorePrintLogBean, Long> {
    public Long insert(CorePrinterPropertiesDTO var1);
}
