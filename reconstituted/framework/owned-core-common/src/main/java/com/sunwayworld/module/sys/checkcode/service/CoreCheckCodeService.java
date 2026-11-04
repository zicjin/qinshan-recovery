/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.checkcode.service;

import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.sys.checkcode.bean.CoreCheckCodeBean;
import java.util.List;

public interface CoreCheckCodeService
extends GenericService<CoreCheckCodeBean, Long> {
    public List<CoreCheckCodeBean> selectCheckableList();

    public List<String> selectCategoryList();
}
