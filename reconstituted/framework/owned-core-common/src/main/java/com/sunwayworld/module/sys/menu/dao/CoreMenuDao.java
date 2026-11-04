/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.dao;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.module.sys.menu.bean.CoreMenuBean;
import java.util.List;
import java.util.Map;

public interface CoreMenuDao
extends GenericDao<CoreMenuBean, String> {
    public List<Map<String, Object>> selectJsonTree(MapperParameter var1);

    public List<Map<String, Object>> selectTreeNodeList(Map<String, Object> var1);
}
