package com.sunwayworld.emc.module.mdm.basemdm.room.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.room.bean.EmcRoomBean;
import com.sunwayworld.emc.module.mdm.basemdm.room.dao.EmcRoomDao;
import com.sunwayworld.emc.module.mdm.basemdm.room.service.EmcRoomService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
* @author zhengxy@sunwayworld.com 2020年07月03日
*/
@Repository
public class EmcRoomServiceImpl implements EmcRoomService {

    @Autowired
    private EmcRoomDao emcRoomDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcRoomDao getDao() {
        return emcRoomDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapperBean) {
        EmcRoomBean emcRoomBean = wrapperBean.parseUnique(EmcRoomBean.class);
        Long id = ApplicationContextHelper.getNextIdentity();
        emcRoomBean.setId(id);
        getDao().insert(emcRoomBean);
        return id;
    }

}
