/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.specialcharacter.dao;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.module.item.specialcharacter.bean.CoreSpecialCharacterBean;
import java.util.List;
import java.util.Map;

public interface CoreSpecialCharacterDao
extends GenericDao<CoreSpecialCharacterBean, Long> {
    public List<Map<String, Object>> selectByChooseQtyDesc(MapperParameter var1);
}
