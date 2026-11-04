/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.design.studio.dao.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.TransactionUtils;
import com.sunwayworld.module.design.studio.bean.CoreStudioTableFieldBean;
import com.sunwayworld.module.design.studio.dao.CoreStudioTableFieldDao;
import com.sunwayworld.module.design.studio.mapper.CoreStudioTableFieldMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreStudioTableFieldDaoImpl
extends MybatisDaoSupport<CoreStudioTableFieldBean, Long>
implements CoreStudioTableFieldDao {
    @Autowired
    private CoreStudioTableFieldMapper tableFieldMapper;

    public CoreStudioTableFieldMapper getMapper() {
        return this.tableFieldMapper;
    }

    @Override
    public void cacheEvict(CoreStudioTableFieldBean oldItem, CoreStudioTableFieldBean newItem) {
        String tableName = oldItem == null ? newItem.getTableName() : oldItem.getTableName();
        RedisHelper.evict("T_CORE_STUDIO_TABLE_FIELD$TABLE", tableName);
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("T_CORE_STUDIO_TABLE_FIELD$TABLE", tableName);
            }
        });
    }
}
