/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.acqconfig.service;

import com.sunwayworld.cloud.module.equipt.acqconfig.bean.CoreEquiptAcqConfigBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericImportService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

public interface CoreEquiptAcqConfigService
extends GenericService<CoreEquiptAcqConfigBean, String>,
GenericChoosableService<CoreEquiptAcqConfigBean, String>,
GenericImportService<CoreEquiptAcqConfigBean, String> {
    public void insertByEquiptId(String var1);

    public String connect(String var1, RestJsonWrapperBean var2);

    public void pushPortMessage(String var1, String var2);
}
