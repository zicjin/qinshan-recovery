/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.persistent.dao.impl;

import com.sunwayworld.cloud.module.les.bean.CoreLesUnitCommentBean;
import com.sunwayworld.cloud.module.les.persistent.dao.CoreLesUnitCommentDao;
import com.sunwayworld.cloud.module.les.persistent.mapper.CoreLesUnitCommentMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreLesUnitCommentDaoImpl
extends MybatisDaoSupport<CoreLesUnitCommentBean, Long>
implements CoreLesUnitCommentDao {
    @Autowired
    private CoreLesUnitCommentMapper coreLesUnitCommentMapper;

    public CoreLesUnitCommentMapper getMapper() {
        return this.coreLesUnitCommentMapper;
    }
}
