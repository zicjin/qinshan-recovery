/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.help.persistent.dao.impl;

import com.sunwayworld.cloud.module.item.help.bean.CoreHelpBean;
import com.sunwayworld.cloud.module.item.help.persistent.dao.CoreHelpDao;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreHelpDaoImpl
extends MybatisDaoSupport<CoreHelpBean, String>
implements CoreHelpDao {
}
