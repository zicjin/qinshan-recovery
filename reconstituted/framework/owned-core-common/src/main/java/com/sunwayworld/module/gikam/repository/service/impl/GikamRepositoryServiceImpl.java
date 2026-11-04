/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.gikam.repository.service.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.gikam.repository.bean.GikamRepositoryStatusDTO;
import com.sunwayworld.module.gikam.repository.service.GikamRepositoryService;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import org.springframework.stereotype.Repository;

@Repository
public class GikamRepositoryServiceImpl
implements GikamRepositoryService {
    private static final int GIKAM_EDITABLE_HOLD_LOCK_SECS = 15;
    private static final String GIKAM_EDITABLE_LOCK_KEY = "GIKAM_EDITABLE";

    @Override
    public GikamRepositoryStatusDTO markItemEditable(String key) {
        String loginUserId = LocalContextHelper.getLoginUserId();
        GikamRepositoryStatusDTO repositoryStatus = new GikamRepositoryStatusDTO();
        if (StringUtils.isEmpty(loginUserId)) {
            repositoryStatus.setEditable(false);
            return repositoryStatus;
        }
        if (RedisHelper.putIfAbsent(GIKAM_EDITABLE_LOCK_KEY, key, loginUserId, 15)) {
            repositoryStatus.setEditable(true);
            repositoryStatus.setLockUserId(loginUserId);
            repositoryStatus.setLockUserName(LocalContextHelper.getLoginUserName());
            return repositoryStatus;
        }
        String lockUserId = (String)RedisHelper.get(GIKAM_EDITABLE_LOCK_KEY, key);
        if (loginUserId.equals(lockUserId)) {
            RedisHelper.expire(GIKAM_EDITABLE_LOCK_KEY, key, 15);
            repositoryStatus.setEditable(true);
            repositoryStatus.setLockUserId(loginUserId);
            repositoryStatus.setLockUserName(LocalContextHelper.getLoginUserName());
            return repositoryStatus;
        }
        repositoryStatus.setEditable(false);
        CoreUserBean lockUser = (CoreUserBean)ApplicationContextHelper.getBean(CoreUserService.class).selectById(lockUserId);
        repositoryStatus.setLockUserId(lockUser.getId());
        repositoryStatus.setLockUserName(lockUser.getUserName());
        return repositoryStatus;
    }
}
