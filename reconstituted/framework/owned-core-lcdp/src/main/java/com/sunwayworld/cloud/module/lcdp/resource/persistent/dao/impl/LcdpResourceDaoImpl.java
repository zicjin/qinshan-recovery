/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceBean;
import com.sunwayworld.cloud.module.lcdp.resource.persistent.dao.LcdpResourceDao;
import com.sunwayworld.cloud.module.lcdp.resource.persistent.mapper.LcdpResourceMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpResourceDaoImpl
extends MybatisDaoSupport<LcdpResourceBean, Long>
implements LcdpResourceDao {
    @Autowired
    private LcdpResourceMapper lcdpResourceMapper;

    public LcdpResourceMapper getMapper() {
        return this.lcdpResourceMapper;
    }

    @Override
    public List<Map<String, Object>> selectTreeChildQtyByParentId(List<Long> parentIdList) {
        return this.getMapper().selectTreeChildQtyByParentId(parentIdList);
    }

    @Override
    public List<Map<String, Object>> selectPageByCondition(MapperParameter parameter) {
        return this.getMapper().selectPageByCondition(parameter);
    }
}
