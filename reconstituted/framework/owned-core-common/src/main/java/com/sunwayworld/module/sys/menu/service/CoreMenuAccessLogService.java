/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.service;

import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.sys.menu.bean.CoreMenuAccessLogBean;
import java.util.List;

public interface CoreMenuAccessLogService
extends GenericService<CoreMenuAccessLogBean, Long> {
    public void insertMenuAccessLog(String var1, String var2, Long var3);

    public List<String> selectHighPriorityMenuIdList();
}
