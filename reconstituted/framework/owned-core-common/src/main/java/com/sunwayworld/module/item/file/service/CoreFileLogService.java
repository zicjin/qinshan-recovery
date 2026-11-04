/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.file.service;

import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.item.file.bean.CoreFileLogBean;
import java.util.List;

public interface CoreFileLogService
extends GenericService<CoreFileLogBean, Long> {
    public Long insert(Long var1, String var2);

    public Long insert(List<Long> var1, String var2);
}
