/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.dingtalk.service.impl;

import com.sunwayworld.cloud.module.mobile.dingtalk.bean.DingtalkUserBean;
import com.sunwayworld.cloud.module.mobile.dingtalk.persistent.dao.DingtalkUserDao;
import com.sunwayworld.cloud.module.mobile.dingtalk.service.DingtalkUserService;
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
public class DingtalkUserServiceImpl
implements DingtalkUserService {
    @Autowired
    private DingtalkUserDao dingtalkUserDao;

    @Override
    public DingtalkUserDao getDao() {
        return this.dingtalkUserDao;
    }

    @Override
    public void insertPushUser(String agentid, String userId, String dtuserid) {
        if (!StringUtils.isEmpty(userId) && !StringUtils.isEmpty(dtuserid)) {
            DingtalkUserBean pushUserBean = new DingtalkUserBean();
            pushUserBean.setId(userId);
            pushUserBean.setDtuserid(dtuserid);
            pushUserBean.setAgentid(agentid);
            boolean exist = false;
            List<DingtalkUserBean> pushUserList = this.getDao().selectList(pushUserBean, new Order[0]);
            for (DingtalkUserBean pushUser : pushUserList) {
                if (!pushUserBean.getDtuserid().equals(pushUser.getDtuserid()) || !pushUserBean.getId().equals(pushUser.getId())) {
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
        DingtalkUserBean pushUserBean = jsonWrapper.parseUnique(DingtalkUserBean.class);
        boolean exist = false;
        List<DingtalkUserBean> pushUserList = this.getDao().selectList(pushUserBean, new Order[0]);
        for (DingtalkUserBean pushUser : pushUserList) {
            if (!pushUserBean.getDtuserid().equals(pushUser.getDtuserid()) || !pushUserBean.getId().equals(pushUser.getId())) {
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
