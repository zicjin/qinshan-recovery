// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.item.admarea.service.impl;

import com.sunwayworld.emc.module.item.admarea.bean.EmcAdmAreaBean;
import com.sunwayworld.emc.module.item.admarea.dao.EmcAdmAreaDao;
import com.sunwayworld.emc.module.item.admarea.service.EmcAdmAreaService;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class EmcAdmAreaServiceImpl
implements EmcAdmAreaService {
    @Autowired
    private EmcAdmAreaDao emcAdmAreaDao;

    @Override
    public EmcAdmAreaDao getDao() {
        return this.emcAdmAreaDao;
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.INSERT")
    public String insert(RestJsonWrapperBean jsonWrapper) {
        EmcAdmAreaBean emcAdmArea = jsonWrapper.parseUnique(EmcAdmAreaBean.class);
        emcAdmArea.setId(ApplicationContextHelper.getNextSequence(this.getDao().getTable()));
        this.getDao().insert(emcAdmArea);
        return emcAdmArea.getId();
    }
}
