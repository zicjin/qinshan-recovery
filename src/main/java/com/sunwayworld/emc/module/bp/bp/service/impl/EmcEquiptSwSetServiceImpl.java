// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.bp.bp.service.impl;

import com.sunwayworld.emc.module.bp.bp.bean.EmcEquiptSwSetBean;
import com.sunwayworld.emc.module.bp.bp.dao.EmcEquiptSwSetDao;
import com.sunwayworld.emc.module.bp.bp.service.EmcEquiptSwSetService;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class EmcEquiptSwSetServiceImpl
implements EmcEquiptSwSetService {
    @Autowired
    private EmcEquiptSwSetDao emcEquiptSwSetDao;

    @Override
    public EmcEquiptSwSetDao getDao() {
        return this.emcEquiptSwSetDao;
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        List<EmcEquiptSwSetBean> beanList = jsonWrapper.parse(EmcEquiptSwSetBean.class);
        for (EmcEquiptSwSetBean emcEquiptSwSet : beanList) {
            emcEquiptSwSet.setId(ApplicationContextHelper.getNextIdentity());
        }
        this.getDao().insert(beanList);
        return beanList.get(0).getId();
    }
}
