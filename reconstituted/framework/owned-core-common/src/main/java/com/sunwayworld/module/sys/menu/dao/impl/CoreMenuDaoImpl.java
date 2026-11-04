/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.dao.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.TransactionUtils;
import com.sunwayworld.module.sys.menu.bean.CoreMenuBean;
import com.sunwayworld.module.sys.menu.dao.CoreMenuDao;
import com.sunwayworld.module.sys.menu.mapper.CoreMenuMapper;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreMenuDaoImpl
extends MybatisDaoSupport<CoreMenuBean, String>
implements CoreMenuDao {
    @Autowired
    private CoreMenuMapper menuMapper;

    public CoreMenuMapper getMapper() {
        return this.menuMapper;
    }

    @Override
    public List<Map<String, Object>> selectJsonTree(MapperParameter parameter) {
        return this.menuMapper.selectJsonTree(parameter);
    }

    @Override
    public List<Map<String, Object>> selectTreeNodeList(Map<String, Object> parameter) {
        return this.getMapper().selectTreeNodeList(parameter);
    }

    @Override
    @CacheEvict(value={"T_CORE_MENU"}, key="'ALL'")
    public void cacheEvict(CoreMenuBean oldItem, CoreMenuBean newItem) {
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("T_CORE_MENU", "ALL");
            }
        });
    }
}
