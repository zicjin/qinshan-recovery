/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.persistent.dao;

import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;
import java.util.List;
import java.util.Map;

public interface LcdpResourceDao
extends GenericDao<LcdpResourceBean, Long> {
    public List<Map<String, Object>> selectTreeChildQtyByParentId(List<Long> var1);

    public List<Map<String, Object>> selectPageByCondition(MapperParameter var1);
}
