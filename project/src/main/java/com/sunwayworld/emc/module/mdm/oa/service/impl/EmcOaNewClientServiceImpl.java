// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.mdm.oa.service.impl;

import com.sunwayworld.emc.module.mdm.oa.bean.EmcOaNewClientBean;
import com.sunwayworld.emc.module.mdm.oa.dao.EmcOaNewClientDao;
import com.sunwayworld.emc.module.mdm.oa.service.EmcOaNewClientService;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class EmcOaNewClientServiceImpl
implements EmcOaNewClientService {
    @Autowired
    private EmcOaNewClientDao emcOaNewClientDao;

    @Override
    public EmcOaNewClientDao getDao() {
        return this.emcOaNewClientDao;
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcOaNewClientBean emcOaNewClient = jsonWrapper.parseUnique(EmcOaNewClientBean.class);
        emcOaNewClient.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(emcOaNewClient);
        return emcOaNewClient.getId();
    }
}
