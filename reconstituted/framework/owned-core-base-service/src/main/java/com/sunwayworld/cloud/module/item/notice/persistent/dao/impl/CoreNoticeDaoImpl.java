/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.notice.persistent.dao.impl;

import com.sunwayworld.cloud.module.item.notice.bean.CoreNoticeBean;
import com.sunwayworld.cloud.module.item.notice.persistent.dao.CoreNoticeDao;
import com.sunwayworld.cloud.module.item.notice.persistent.mapper.CoreNoticeMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreNoticeDaoImpl
extends MybatisDaoSupport<CoreNoticeBean, Long>
implements CoreNoticeDao {
    @Autowired
    private CoreNoticeMapper noticeMapper;

    public CoreNoticeMapper getMapper() {
        return this.noticeMapper;
    }

    @Override
    public List<Map<String, Object>> selectIndexByCondition(MapperParameter parameter) {
        return this.getMapper().selectIndexByCondition(parameter);
    }
}
