package com.sunwayworld.emc.module.item.homeattachment.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.item.homeattachment.bean.EmcHomeAttachmentPermissionBean;
import com.sunwayworld.emc.module.item.homeattachment.dao.EmcHomeAttachmentPermissionDao;
import com.sunwayworld.emc.module.item.homeattachment.mapper.EmcHomeAttachmentPermissionMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcHomeAttachmentPermissionDaoImpl extends MybatisDaoSupport<EmcHomeAttachmentPermissionBean, Long> implements EmcHomeAttachmentPermissionDao {

    @Autowired
    private EmcHomeAttachmentPermissionMapper emcHomeAttachmentPermissionMapper;

    @Override
    public EmcHomeAttachmentPermissionMapper getMapper() {
        return emcHomeAttachmentPermissionMapper;
    }

}
