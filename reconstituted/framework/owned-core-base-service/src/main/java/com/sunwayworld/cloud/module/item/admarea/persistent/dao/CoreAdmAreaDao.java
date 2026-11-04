/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.admarea.persistent.dao;

import com.sunwayworld.cloud.module.item.admarea.bean.CoreAdmAreaBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;
import java.util.List;

public interface CoreAdmAreaDao
extends GenericDao<CoreAdmAreaBean, String> {
    public List<CoreAdmAreaBean> selectTreeNodeList(MapperParameter var1);
}
