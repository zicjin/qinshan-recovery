/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.eln.service;

import com.sunwayworld.cloud.module.eln.bean.CoreElnAtRecordBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface CoreElnAtRecordService
extends GenericService<CoreElnAtRecordBean, Long> {
    public void insert(Long var1, String var2);
}
