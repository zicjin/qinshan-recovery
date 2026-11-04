/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.help.service.impl;

import com.sunwayworld.cloud.module.item.help.bean.CoreHelpBean;
import com.sunwayworld.cloud.module.item.help.persistent.dao.CoreHelpDao;
import com.sunwayworld.cloud.module.item.help.service.CoreHelpService;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreHelpServiceImpl
implements CoreHelpService {
    @Autowired
    private CoreHelpDao helpDao;

    @Override
    public CoreHelpDao getDao() {
        return this.helpDao;
    }

    @Override
    @Transactional
    public CoreHelpBean selectDetail(String id) {
        if (this.getDao().selectByIdIfPresent(id) == null) {
            CoreHelpBean item = new CoreHelpBean();
            item.setId(id);
            this.getDao().insert(item);
        }
        return (CoreHelpBean)this.getDao().selectById(id);
    }

    @Override
    public boolean isUpdatedByCurrentUser(String id) {
        CoreUserBean currentUser = LocalContextHelper.getLoginUser();
        return "orgAdmin".equals(currentUser.getUserType()) || "sysAdmin".equals(currentUser.getUserType());
    }
}
