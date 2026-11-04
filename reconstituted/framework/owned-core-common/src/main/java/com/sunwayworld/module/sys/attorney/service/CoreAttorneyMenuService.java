/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.attorney.service;

import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.sys.attorney.bean.CoreAttorneyMenuBean;
import java.util.List;

public interface CoreAttorneyMenuService
extends GenericService<CoreAttorneyMenuBean, Long> {
    public List<CoreAttorneyMenuBean> selectListByAttoryneyId(Long var1);
}
