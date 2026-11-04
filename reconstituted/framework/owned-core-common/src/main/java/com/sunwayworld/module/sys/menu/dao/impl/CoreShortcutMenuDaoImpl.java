/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.dao.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.TransactionUtils;
import com.sunwayworld.module.sys.menu.bean.CoreShortcutMenuBean;
import com.sunwayworld.module.sys.menu.dao.CoreShortcutMenuDao;
import com.sunwayworld.module.sys.menu.mapper.CoreShortcutMenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreShortcutMenuDaoImpl
extends MybatisDaoSupport<CoreShortcutMenuBean, Long>
implements CoreShortcutMenuDao {
    @Autowired
    private CoreShortcutMenuMapper coreShortcutMenuMapper;

    public CoreShortcutMenuMapper getMapper() {
        return this.coreShortcutMenuMapper;
    }

    @Override
    @CacheEvict(value={"T_CORE_SHORTCUT_MENU.BY_LOGIN_USER_ID"}, keyGenerator="loginUserKeyGenerator")
    public void cacheEvict(CoreShortcutMenuBean oldItem, CoreShortcutMenuBean newItem) {
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("T_CORE_SHORTCUT_MENU.BY_LOGIN_USER_ID", LocalContextHelper.getLoginUserId());
            }
        });
    }
}
