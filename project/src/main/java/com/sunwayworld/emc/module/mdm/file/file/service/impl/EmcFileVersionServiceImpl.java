package com.sunwayworld.emc.module.mdm.file.file.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.file.file.bean.EmcFileVersionBean;
import com.sunwayworld.emc.module.mdm.file.file.dao.EmcFileVersionDao;
import com.sunwayworld.emc.module.mdm.file.file.service.EmcFileVersionService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author zhengxy@sunwayworld.com 2019年10月09日
 */
@Repository
public class EmcFileVersionServiceImpl implements EmcFileVersionService {

    @Autowired
    private EmcFileVersionDao emcFileVersionDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcFileVersionDao getDao() {
        return emcFileVersionDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(Long fileId, RestJsonWrapperBean wrapper) {
        EmcFileVersionBean emcFileVersionBean = wrapper.parseUnique(EmcFileVersionBean.class);
        Long id = ApplicationContextHelper.getNextIdentity();
        emcFileVersionBean.setId(id);
        emcFileVersionBean.setFileId(fileId);
        getDao().insert(emcFileVersionBean);
        return id;
    }

}
