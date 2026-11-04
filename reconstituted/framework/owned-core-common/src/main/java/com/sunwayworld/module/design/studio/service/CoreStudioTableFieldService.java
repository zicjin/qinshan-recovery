/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.design.studio.service;

import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.design.studio.bean.CoreStudioTableFieldBean;
import java.util.List;

public interface CoreStudioTableFieldService
extends GenericService<CoreStudioTableFieldBean, Long> {
    public List<CoreStudioTableFieldBean> selectListByTable(String var1);

    public void reset(List<CoreStudioTableFieldBean> var1);
}
