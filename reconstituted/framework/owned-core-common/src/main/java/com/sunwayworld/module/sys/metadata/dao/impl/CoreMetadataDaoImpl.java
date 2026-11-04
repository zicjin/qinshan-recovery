/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.metadata.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.metadata.bean.CoreMetadataBean;
import com.sunwayworld.module.sys.metadata.dao.CoreMetadataDao;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreMetadataDaoImpl
extends MybatisDaoSupport<CoreMetadataBean, Long>
implements CoreMetadataDao {
}
