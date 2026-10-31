package com.sunwayworld.emc.module.mdm.file.fileapply.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.file.fileapply.bean.EmcFileApplyBean;
import com.sunwayworld.emc.module.mdm.file.fileapply.dao.EmcFileApplyDao;
import com.sunwayworld.emc.module.mdm.file.fileapply.mapper.EmcFileApplyMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author zhengxy@sunwayworld.com 2019年10月09日
 */
@Repository
public class EmcFileApplyDaoImpl extends MybatisDaoSupport<EmcFileApplyBean, Long> implements EmcFileApplyDao {

    @Autowired
    private EmcFileApplyMapper emcFileApplyMapper;

    @Override
    public EmcFileApplyMapper getMapper() {
        return emcFileApplyMapper;
    }
}
