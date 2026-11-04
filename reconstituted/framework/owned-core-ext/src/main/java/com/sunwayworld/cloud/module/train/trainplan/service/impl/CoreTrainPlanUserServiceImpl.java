/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.trainplan.service.impl;

import com.sunwayworld.cloud.module.train.trainplan.bean.CoreTrainPlanUserBean;
import com.sunwayworld.cloud.module.train.trainplan.persistent.dao.CoreTrainPlanUserDao;
import com.sunwayworld.cloud.module.train.trainplan.service.CoreTrainPlanUserService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreTrainPlanUserServiceImpl
implements CoreTrainPlanUserService {
    @Autowired
    private CoreTrainPlanUserDao coreTrainPlanUserDao;

    @Override
    public CoreTrainPlanUserDao getDao() {
        return this.coreTrainPlanUserDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean wrapper) {
        List<CoreTrainPlanUserBean> trainPlanUserList = wrapper.parse(CoreTrainPlanUserBean.class);
        if (trainPlanUserList.isEmpty()) {
            return null;
        }
        List<Long> idList = ApplicationContextHelper.getNextIdentityList(trainPlanUserList.size());
        for (int i = 0; i < trainPlanUserList.size(); ++i) {
            Long id = idList.get(i);
            trainPlanUserList.get(i).setId(id);
        }
        this.getDao().insert(trainPlanUserList);
        return idList.get(0);
    }
}
