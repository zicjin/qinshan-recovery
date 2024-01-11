package com.sunwayworld.emc.module.mdm.bizmdm.method.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.bizmdm.method.bean.EmcMethodVersionBean;
import com.sunwayworld.emc.module.mdm.bizmdm.method.dao.EmcMethodVersionDao;
import com.sunwayworld.emc.module.mdm.bizmdm.method.service.EmcMethodService;
import com.sunwayworld.emc.module.mdm.bizmdm.method.service.EmcMethodVersionService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author yuanhh@sunwayworld.com 2019年9月10日
 */
@GikamBean
@Repository
public class EmcMethodVersionServiceImpl implements EmcMethodVersionService {
    @Autowired
    private EmcMethodVersionDao emcMethodVersionDao;

    @Autowired
    @Lazy
    private EmcMethodService methodService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcMethodVersionDao getDao() {
        return emcMethodVersionDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(Long methodId, RestJsonWrapperBean jsonWrapper) {
        EmcMethodVersionBean emcMethodVersion = jsonWrapper.parseUnique(EmcMethodVersionBean.class);
        emcMethodVersion.setId(ApplicationContextHelper.getNextIdentity());
        emcMethodVersion.setMethodId(methodId);
        emcMethodVersion.setVersionNo(getMaxVersionNo(methodId) + 1);
        getDao().insert(emcMethodVersion);
        // 更新方法最大版本信息
        methodService.updateVersionInfo(methodId);
        return emcMethodVersion.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void deleteVersion(Long methodId, RestJsonWrapperBean wrapper) {
        this.delete(wrapper);
        // 更新方法最大版本信息
        methodService.updateVersionInfo(methodId);
    }

    // -------------------------------------------------------------------
    // 私有方法
    // -------------------------------------------------------------------

    private Long getMaxVersionNo(Long methodId) {

        List<EmcMethodVersionBean> methodVersionList = getDao().selectListByOneColumnValue(methodId, "METHODID", Order.desc("VERSIONNO"));

        if (methodVersionList.isEmpty()) {
            return 0l;
        } else {
            return methodVersionList.get(0).getVersionNo();
        }
    }
}
