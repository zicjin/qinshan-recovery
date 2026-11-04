/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.push.service.impl;

import com.sunwayworld.cloud.module.mobile.push.bean.MobilePushUserBean;
import com.sunwayworld.cloud.module.mobile.push.persistent.dao.MobilePushUserDao;
import com.sunwayworld.cloud.module.mobile.push.service.MobilePushUserService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class MobilePushUserServiceImpl
implements MobilePushUserService {
    @Autowired
    private MobilePushUserDao mobileMenuDao;

    @Override
    public MobilePushUserDao getDao() {
        return this.mobileMenuDao;
    }

    @Override
    public void insertPushUser(String userId, String clientId) {
        if (!StringUtils.isEmpty(userId) && !StringUtils.isEmpty(clientId)) {
            MobilePushUserBean pushUserBean = new MobilePushUserBean();
            pushUserBean.setId(userId);
            pushUserBean.setClientId(clientId);
            boolean exist = false;
            List<MobilePushUserBean> pushUserList = this.getDao().selectUnionList(pushUserBean, new Order[0]);
            for (MobilePushUserBean pushUser : pushUserList) {
                if (!pushUserBean.getClientId().equals(pushUser.getClientId()) || !pushUserBean.getId().equals(pushUser.getId())) {
                    this.getDao().delete(pushUser.getId());
                    continue;
                }
                exist = true;
            }
            if (!exist) {
                this.getDao().insert(pushUserBean);
            }
        }
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public String insert(RestJsonWrapperBean jsonWrapper) {
        MobilePushUserBean pushUserBean = jsonWrapper.parseUnique(MobilePushUserBean.class);
        boolean exist = false;
        List<MobilePushUserBean> pushUserList = this.getDao().selectUnionList(pushUserBean, new Order[0]);
        for (MobilePushUserBean pushUser : pushUserList) {
            if (!pushUserBean.getClientId().equals(pushUser.getClientId()) || !pushUserBean.getId().equals(pushUser.getId())) {
                this.getDao().delete(pushUser.getId());
                continue;
            }
            exist = true;
        }
        if (!exist) {
            this.getDao().insert(pushUserBean);
        }
        return pushUserBean.getId();
    }
}
