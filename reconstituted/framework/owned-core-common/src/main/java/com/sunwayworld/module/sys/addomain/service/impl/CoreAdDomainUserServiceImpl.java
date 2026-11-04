/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.addomain.service.impl;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.addomain.bean.CoreAdDomainUserBean;
import com.sunwayworld.module.sys.addomain.dao.CoreAdDomainUserDao;
import com.sunwayworld.module.sys.addomain.service.CoreAdDomainUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreAdDomainUserServiceImpl
implements CoreAdDomainUserService {
    @Autowired
    private CoreAdDomainUserDao coreAdDomainUserDao;

    @Override
    public CoreAdDomainUserDao getDao() {
        return this.coreAdDomainUserDao;
    }

    @Override
    @Transactional
    public String insert(RestJsonWrapperBean wrapper) {
        CoreAdDomainUserBean adDomainUser = wrapper.parseUnique(CoreAdDomainUserBean.class);
        CoreAdDomainUserBean coreAdDomainUser = (CoreAdDomainUserBean)this.getDao().selectByIdIfPresent(adDomainUser.getId());
        if (coreAdDomainUser == null) {
            this.getDao().insert(adDomainUser);
        } else {
            this.getDao().update(adDomainUser, new String[]{"ADUSERID"});
        }
        return adDomainUser.getId();
    }

    @Override
    @Transactional
    public void insert(String id, String adUserId) {
        CoreAdDomainUserBean adDomainUser = new CoreAdDomainUserBean();
        adDomainUser.setId(id);
        adDomainUser.setAdUserId(adUserId);
        this.getDao().insert(adDomainUser);
    }
}
