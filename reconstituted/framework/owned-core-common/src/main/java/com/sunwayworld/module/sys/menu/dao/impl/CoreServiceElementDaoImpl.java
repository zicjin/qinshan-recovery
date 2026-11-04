/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.menu.bean.CoreServiceElementBean;
import com.sunwayworld.module.sys.menu.dao.CoreServiceElementDao;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreServiceElementDaoImpl
extends MybatisDaoSupport<CoreServiceElementBean, Long>
implements CoreServiceElementDao {
}
