/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.specialcharacter.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.specialcharacter.dao.CoreCommonCharacterDao;
import com.sunwayworld.module.item.specialcharacter.service.CoreCommonCharacterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreCommonCharacterServiceImpl
implements CoreCommonCharacterService {
    @Autowired
    private CoreCommonCharacterDao coreCommonCharacterDao;

    @Override
    public CoreCommonCharacterDao getDao() {
        return this.coreCommonCharacterDao;
    }
}
