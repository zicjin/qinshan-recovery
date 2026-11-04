/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.mybatis.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.mybatis.bean.CoreMybatisStatementBean;
import com.sunwayworld.module.sys.mybatis.dao.CoreMybatisStatementDao;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreMybatisStatementDaoImpl
extends MybatisDaoSupport<CoreMybatisStatementBean, Long>
implements CoreMybatisStatementDao {
}
