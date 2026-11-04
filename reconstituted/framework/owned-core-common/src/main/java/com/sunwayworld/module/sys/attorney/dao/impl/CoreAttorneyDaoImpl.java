/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.attorney.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.module.sys.attorney.bean.CoreAttorneyBean;
import com.sunwayworld.module.sys.attorney.dao.CoreAttorneyDao;
import com.sunwayworld.module.sys.attorney.mapper.CoreAttorneyMapper;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreAttorneyDaoImpl
extends MybatisDaoSupport<CoreAttorneyBean, Long>
implements CoreAttorneyDao {
    @Autowired
    private CoreAttorneyMapper attorneyMapper;

    public CoreAttorneyMapper getMapper() {
        return this.attorneyMapper;
    }

    @Override
    public List<Long> selectExecutableAttorneyIdList(String trustorId, String trusteeId) {
        return this.getMapper().selectExecutableAttorneyIdList(trustorId, trusteeId);
    }

    @Override
    public List<CoreAttorneyBean> selectExecutableAttorneyList(String trusteeId) {
        return this.getMapper().selectExecutableAttorneyList(trusteeId).stream().map(m -> PersistableHelper.mapToPersistable(m, CoreAttorneyBean.class)).collect(Collectors.toList());
    }
}
