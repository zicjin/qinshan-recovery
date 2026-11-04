/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.log.service;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericExcelBuilderService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.sys.log.bean.CoreExportLogBean;

public interface CoreExportLogService
extends GenericService<CoreExportLogBean, Long> {
    public Long insert(CoreExportLogBean var1);

    public void insertByExcelDTO(RestJsonWrapperBean var1, GenericExcelBuilderService.DownloadExcelDTO var2, String var3, String var4);
}
