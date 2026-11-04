/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.request.service;

import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.admin.request.bean.CoreRequestUrlBean;
import java.util.List;

public interface CoreRequestUrlService
extends GenericService<CoreRequestUrlBean, Long> {
    public List<CoreRequestUrlBean> selectListByRequestUrl(String var1);
}
