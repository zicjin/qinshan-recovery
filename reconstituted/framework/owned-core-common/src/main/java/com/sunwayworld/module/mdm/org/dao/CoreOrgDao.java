/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.org.dao;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import java.util.List;
import java.util.Map;

public interface CoreOrgDao
extends GenericDao<CoreOrgBean, String> {
    public List<String> selectDescendantIdList(List<String> var1);

    public List<Map<String, Object>> selectRoleOrgList(Map<String, Object> var1);

    public List<CoreOrgBean> selectTreeNodeList(MapperParameter var1);
}
