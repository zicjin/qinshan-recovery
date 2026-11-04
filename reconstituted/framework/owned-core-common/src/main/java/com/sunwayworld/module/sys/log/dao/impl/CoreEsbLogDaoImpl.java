/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.log.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.log.bean.CoreEsbLogBean;
import com.sunwayworld.module.sys.log.dao.CoreEsbLogDao;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreEsbLogDaoImpl
extends MybatisDaoSupport<CoreEsbLogBean, Long>
implements CoreEsbLogDao {
}
