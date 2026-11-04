/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.diagram.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.diagram.bean.CoreDiagramBean;
import com.sunwayworld.module.item.diagram.dao.CoreDiagramDao;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreDiagramDaoImpl
extends MybatisDaoSupport<CoreDiagramBean, Long>
implements CoreDiagramDao {
}
