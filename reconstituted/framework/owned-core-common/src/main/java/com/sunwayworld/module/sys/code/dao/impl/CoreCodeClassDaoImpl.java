/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.code.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.code.bean.CoreCodeClassBean;
import com.sunwayworld.module.sys.code.dao.CoreCodeClassDao;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreCodeClassDaoImpl
extends MybatisDaoSupport<CoreCodeClassBean, String>
implements CoreCodeClassDao {
}
