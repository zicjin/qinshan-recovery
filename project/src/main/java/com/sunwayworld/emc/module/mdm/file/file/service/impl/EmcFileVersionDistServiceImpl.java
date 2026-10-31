package com.sunwayworld.emc.module.mdm.file.file.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.file.file.bean.EmcFileVersionDistBean;
import com.sunwayworld.emc.module.mdm.file.file.dao.EmcFileVersionDistDao;
import com.sunwayworld.emc.module.mdm.file.file.service.EmcFileVersionDistService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author zhengxy@sunwayworld.com 2019年10月09日
 */
@Repository
public class EmcFileVersionDistServiceImpl implements EmcFileVersionDistService {

    @Autowired
    private EmcFileVersionDistDao emcFileVersionDistDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcFileVersionDistDao getDao() {
        return emcFileVersionDistDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(Long fileVersionId, RestJsonWrapperBean wrapper) {
        EmcFileVersionDistBean emcFileVersionDistBean = wrapper.parseUnique(EmcFileVersionDistBean.class);
        Long id = ApplicationContextHelper.getNextIdentity();
        emcFileVersionDistBean.setId(id);
        emcFileVersionDistBean.setFileVersionId(fileVersionId);
        getDao().insert(emcFileVersionDistBean);
        return id;
    }
}
