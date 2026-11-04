/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.admarea.persistent.dao.impl;

import com.sunwayworld.cloud.module.item.admarea.bean.CoreAdmAreaBean;
import com.sunwayworld.cloud.module.item.admarea.persistent.dao.CoreAdmAreaDao;
import com.sunwayworld.cloud.module.item.admarea.persistent.mapper.CoreAdmAreaMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreAdmAreaDaoImpl
extends MybatisDaoSupport<CoreAdmAreaBean, String>
implements CoreAdmAreaDao {
    @Autowired
    private CoreAdmAreaMapper admAreaMapper;

    public CoreAdmAreaMapper getMapper() {
        return this.admAreaMapper;
    }

    @Override
    public List<CoreAdmAreaBean> selectTreeNodeList(MapperParameter parameter) {
        return this.getMapper().selectTreeNodeList(parameter).stream().map(e -> PersistableHelper.mapToPersistable(e, CoreAdmAreaBean.class)).collect(Collectors.toList());
    }
}
