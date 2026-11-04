/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.dingtalk.persistent.dao.impl;

import com.sunwayworld.cloud.module.mobile.dingtalk.bean.DingtalkMessageBean;
import com.sunwayworld.cloud.module.mobile.dingtalk.persistent.dao.DingtalkMessageDao;
import com.sunwayworld.cloud.module.mobile.dingtalk.persistent.mapper.DingtalkMessageMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class DingtalkMessageDaoImpl
extends MybatisDaoSupport<DingtalkMessageBean, Long>
implements DingtalkMessageDao {
    @Autowired
    private DingtalkMessageMapper mobilePushmessageMapper;

    public DingtalkMessageMapper getMapper() {
        return this.mobilePushmessageMapper;
    }
}
