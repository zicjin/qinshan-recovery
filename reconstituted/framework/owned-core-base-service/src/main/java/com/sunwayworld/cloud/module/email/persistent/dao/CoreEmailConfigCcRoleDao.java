/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.email.persistent.dao;

import com.sunwayworld.cloud.module.email.bean.CoreEmailConfigCcRoleBean;
import com.sunwayworld.framework.support.base.dao.GenericDao;
import java.util.List;

public interface CoreEmailConfigCcRoleDao
extends GenericDao<CoreEmailConfigCcRoleBean, Long> {
    public List<String> selectUserListByConfig(String var1);
}
