/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.notice.persistent.dao;

import com.sunwayworld.cloud.module.item.notice.bean.CoreNoticeBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;
import java.util.List;
import java.util.Map;

public interface CoreNoticeDao
extends GenericDao<CoreNoticeBean, Long> {
    public List<Map<String, Object>> selectIndexByCondition(MapperParameter var1);
}
