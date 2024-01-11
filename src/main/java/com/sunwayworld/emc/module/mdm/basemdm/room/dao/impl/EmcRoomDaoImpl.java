package com.sunwayworld.emc.module.mdm.basemdm.room.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.room.bean.EmcRoomBean;
import com.sunwayworld.emc.module.mdm.basemdm.room.dao.EmcRoomDao;
import com.sunwayworld.emc.module.mdm.basemdm.room.mapper.EmcRoomMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
* @author zhengxy@sunwayworld.com 2020年07月03日
*/
@Repository
public class EmcRoomDaoImpl extends MybatisDaoSupport<EmcRoomBean, Long> implements EmcRoomDao {

    @Autowired
    private EmcRoomMapper emcRoomMapper;

    @Override
    public EmcRoomMapper getMapper() {
        return emcRoomMapper;
    }
}
