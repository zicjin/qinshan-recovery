/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.metadata.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.metadata.bean.CoreTableHierarchyBean;
import com.sunwayworld.module.sys.metadata.dao.CoreTableHierarchyDao;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreTableHierarchyDaoImpl
extends MybatisDaoSupport<CoreTableHierarchyBean, Long>
implements CoreTableHierarchyDao {
}
