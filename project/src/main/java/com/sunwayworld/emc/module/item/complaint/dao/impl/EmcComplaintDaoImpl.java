package com.sunwayworld.emc.module.item.complaint.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.item.complaint.bean.EmcComplaintBean;
import com.sunwayworld.emc.module.item.complaint.dao.EmcComplaintDao;
import com.sunwayworld.emc.module.item.complaint.mapper.EmcComplaintMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author guok@sunwayworld.com
 * @date 2019-12-13
 */

@Repository
@GikamBean
public class EmcComplaintDaoImpl extends MybatisDaoSupport<EmcComplaintBean, Long>implements EmcComplaintDao {
    @Autowired
    private EmcComplaintMapper complaintMapper;

    @Override
    public EmcComplaintMapper getMapper() {
        return complaintMapper;
    }
}
