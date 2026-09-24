// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.bp.bp.service.impl;

import com.sunwayworld.emc.module.bp.bp.bean.EmcEquiptSetBean;
import com.sunwayworld.emc.module.bp.bp.dao.EmcEquiptSetDao;
import com.sunwayworld.emc.module.bp.bp.service.EmcEquiptSetService;
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
public class EmcEquiptSetServiceImpl
implements EmcEquiptSetService {
    @Autowired
    private EmcEquiptSetDao emcEquiptSetDao;

    @Override
    public EmcEquiptSetDao getDao() {
        return this.emcEquiptSetDao;
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        List<EmcEquiptSetBean> emcEquiptSets = jsonWrapper.parse(EmcEquiptSetBean.class);
        emcEquiptSets.stream().forEach(s -> s.setId(ApplicationContextHelper.getNextIdentity()));
        this.getDao().insert(emcEquiptSets);
        return 1L;
    }
}
