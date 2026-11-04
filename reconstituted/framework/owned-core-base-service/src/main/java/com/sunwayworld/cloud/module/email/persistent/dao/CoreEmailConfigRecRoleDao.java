/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.email.persistent.dao;

import com.sunwayworld.cloud.module.email.bean.CoreEmailConfigRecRoleBean;
import com.sunwayworld.framework.support.base.dao.GenericDao;
import java.util.List;

public interface CoreEmailConfigRecRoleDao
extends GenericDao<CoreEmailConfigRecRoleBean, Long> {
    public List<String> selectUserListByConfig(String var1);
}
