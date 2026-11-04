/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.menu.persistent.dao;

import com.sunwayworld.cloud.module.mobile.menu.bean.MobilePageBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;
import java.util.List;
import java.util.Map;

public interface MobilePageDao
extends GenericDao<MobilePageBean, Long> {
    public List<Map<String, Object>> selectMobilePageTreeList(MapperParameter var1);
}
