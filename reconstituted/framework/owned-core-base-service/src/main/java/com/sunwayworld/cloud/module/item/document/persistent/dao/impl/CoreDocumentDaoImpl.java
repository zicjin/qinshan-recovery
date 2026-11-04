/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.document.persistent.dao.impl;

import com.sunwayworld.cloud.module.item.document.bean.CoreDocumentBean;
import com.sunwayworld.cloud.module.item.document.persistent.dao.CoreDocumentDao;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreDocumentDaoImpl
extends MybatisDaoSupport<CoreDocumentBean, Long>
implements CoreDocumentDao {
}
