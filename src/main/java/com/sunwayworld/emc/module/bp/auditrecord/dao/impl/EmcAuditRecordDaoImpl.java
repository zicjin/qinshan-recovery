package com.sunwayworld.emc.module.bp.auditrecord.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.auditrecord.bean.EmcAuditRecordBean;
import com.sunwayworld.emc.module.bp.auditrecord.dao.EmcAuditRecordDao;
import com.sunwayworld.emc.module.bp.auditrecord.mapper.EmcAuditRecordMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-11-07
 */
@Repository
public class EmcAuditRecordDaoImpl extends MybatisDaoSupport<EmcAuditRecordBean, Long>implements EmcAuditRecordDao {

    @Autowired
    private EmcAuditRecordMapper emcAuditRecordMapper;

    @Override
    public EmcAuditRecordMapper getMapper() {
        return emcAuditRecordMapper;
    }

}
