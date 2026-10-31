// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.bp.bp.service.impl;

import com.sunwayworld.emc.module.bp.bp.bean.EmcEquiptDioxinSetBean;
import com.sunwayworld.emc.module.bp.bp.dao.EmcEquiptDioxinSetDao;
import com.sunwayworld.emc.module.bp.bp.service.EmcEquiptDioxinSetService;
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
public class EmcEquiptDioxinSetServiceImpl
implements EmcEquiptDioxinSetService {
    @Autowired
    private EmcEquiptDioxinSetDao emcEquiptDioxinSetDao;

    @Override
    public EmcEquiptDioxinSetDao getDao() {
        return this.emcEquiptDioxinSetDao;
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        List<EmcEquiptDioxinSetBean> list = jsonWrapper.parse(EmcEquiptDioxinSetBean.class);
        list.stream().forEach(s -> s.setId(ApplicationContextHelper.getNextIdentity()));
        this.getDao().insert(list);
        return list.get(0).getId();
    }
}
