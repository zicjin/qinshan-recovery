/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.specialcharacter.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.specialcharacter.bean.CoreSpecialCharacterBean;
import com.sunwayworld.module.item.specialcharacter.dao.CoreSpecialCharacterDao;
import com.sunwayworld.module.item.specialcharacter.mapper.CoreSpecialCharacterMapper;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreSpecialCharacterDaoImpl
extends MybatisDaoSupport<CoreSpecialCharacterBean, Long>
implements CoreSpecialCharacterDao {
    @Autowired
    private CoreSpecialCharacterMapper coreSpecialCharacterMapper;

    public CoreSpecialCharacterMapper getMapper() {
        return this.coreSpecialCharacterMapper;
    }

    @Override
    public List<Map<String, Object>> selectByChooseQtyDesc(MapperParameter parameter) {
        return this.getMapper().selectByChooseQtyDesc(parameter);
    }
}
