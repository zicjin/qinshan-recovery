/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.attorney.dao;

import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.module.sys.attorney.bean.CoreAttorneyBean;
import java.util.List;

public interface CoreAttorneyDao
extends GenericDao<CoreAttorneyBean, Long> {
    public List<Long> selectExecutableAttorneyIdList(String var1, String var2);

    public List<CoreAttorneyBean> selectExecutableAttorneyList(String var1);
}
