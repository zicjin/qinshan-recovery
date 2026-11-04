/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.zwdd.service.impl;

import com.sunwayworld.cloud.module.mobile.zwdd.bean.ZwddUserBean;
import com.sunwayworld.cloud.module.mobile.zwdd.persistent.dao.ZwddUserDao;
import com.sunwayworld.cloud.module.mobile.zwdd.service.ZwddUserService;
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
public class ZwddUserServiceImpl
implements ZwddUserService {
    @Autowired
    private ZwddUserDao zwddUserDao;

    @Override
    public ZwddUserDao getDao() {
        return this.zwddUserDao;
    }

    @Override
    public void insertPushUser(String agentid, String userId, String dtuserid, String tenantid, String accountid) {
        if (!StringUtils.isEmpty(userId) && !StringUtils.isEmpty(dtuserid)) {
            ZwddUserBean pushUserBean = new ZwddUserBean();
            pushUserBean.setId(userId);
            pushUserBean.setDtuserid(dtuserid);
            pushUserBean.setAgentid(agentid);
            pushUserBean.setTenantid(tenantid);
            pushUserBean.setAccountid(accountid);
            boolean exist = false;
            List<ZwddUserBean> pushUserList = this.getDao().selectList(pushUserBean, new Order[0]);
            for (ZwddUserBean pushUser : pushUserList) {
                if (!pushUserBean.getDtuserid().equals(pushUser.getDtuserid()) || !pushUserBean.getId().equals(pushUser.getId())) {
                    this.getDao().delete(pushUser.getId());
                    continue;
                }
                exist = true;
            }
            if (!exist) {
                try {
                    this.getDao().insert(pushUserBean);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public String insert(RestJsonWrapperBean jsonWrapper) {
        ZwddUserBean pushUserBean = jsonWrapper.parseUnique(ZwddUserBean.class);
        boolean exist = false;
        List<ZwddUserBean> pushUserList = this.getDao().selectList(pushUserBean, new Order[0]);
        for (ZwddUserBean pushUser : pushUserList) {
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
