package com.sunwayworld.emc.module.item.homeattachment.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.item.homeattachment.bean.EmcHomeAttachmentBean;
import com.sunwayworld.emc.module.item.homeattachment.dao.EmcHomeAttachmentDao;
import com.sunwayworld.emc.module.item.homeattachment.mapper.EmcHomeAttachmentMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcHomeAttachmentDaoImpl extends MybatisDaoSupport<EmcHomeAttachmentBean, Long> implements EmcHomeAttachmentDao {

    @Autowired
    private EmcHomeAttachmentMapper emcHomeAttachmentMapper;

    @Override
    public EmcHomeAttachmentMapper getMapper() {
        return emcHomeAttachmentMapper;
    }

}
