/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.user.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.module.mdm.user.bean.CoreModifiedUserPermissionBean;
import com.sunwayworld.module.mdm.user.dao.CoreModifiedUserPermissionDao;
import com.sunwayworld.module.mdm.user.mapper.CoreModifiedUserPermissionMapper;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreModifiedUserPermissionDaoImpl
extends MybatisDaoSupport<CoreModifiedUserPermissionBean, Long>
implements CoreModifiedUserPermissionDao {
    @Autowired
    private CoreModifiedUserPermissionMapper modifiedUserPermissionMapper;

    public CoreModifiedUserPermissionMapper getMapper() {
        return this.modifiedUserPermissionMapper;
    }

    @Override
    public List<CoreModifiedUserPermissionBean> selectOrgPermissionList(MapperParameter parameter) {
        return this.getMapper().selectOrgPermissionList(parameter).stream().map(m -> PersistableHelper.mapToPersistable(m, CoreModifiedUserPermissionBean.class)).collect(Collectors.toList());
    }
}
