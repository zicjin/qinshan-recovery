/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.log.service;

import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.sys.log.bean.CoreLogBean;
import java.util.List;

public interface CoreLogService
extends GenericService<CoreLogBean, Long> {
    public void insertLog(List<CoreLogBean> var1);
}
