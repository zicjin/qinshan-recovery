/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.specialcharacter.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.specialcharacter.bean.CoreCommonCharacterBean;
import com.sunwayworld.module.item.specialcharacter.dao.CoreCommonCharacterDao;
import com.sunwayworld.module.item.specialcharacter.mapper.CoreCommonCharacterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreCommonCharacterDaoImpl
extends MybatisDaoSupport<CoreCommonCharacterBean, Long>
implements CoreCommonCharacterDao {
    @Autowired
    private CoreCommonCharacterMapper coreCommonCharacterMapper;

    public CoreCommonCharacterMapper getMapper() {
        return this.coreCommonCharacterMapper;
    }
}
