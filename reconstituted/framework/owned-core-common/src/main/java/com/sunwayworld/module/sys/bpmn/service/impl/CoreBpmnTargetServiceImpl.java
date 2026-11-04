/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.service.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnTargetBean;
import com.sunwayworld.module.sys.bpmn.dao.CoreBpmnTargetDao;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnTargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreBpmnTargetServiceImpl
implements CoreBpmnTargetService {
    @Autowired
    private CoreBpmnTargetDao targetDao;

    @Override
    public CoreBpmnTargetDao getDao() {
        return this.targetDao;
    }

    @Override
    public Page<CoreBpmnTargetBean> selectPagination(MapperParameter parameter, PageRowBounds rowBounds) {
        Page<CoreBpmnTargetBean> targetList = CoreBpmnTargetService.super.selectPagination(parameter, rowBounds);
        targetList.getRows().forEach(r -> {
            String href = r.getExt$Item("href");
            if (!StringUtils.isEmpty(href)) {
                r.setExt$Item("href", href.substring(0, href.indexOf("/page/")));
            }
        });
        return targetList;
    }

    @Override
    public String getLcdpUrl(String tableName) {
        String path = (String)RedisHelper.get("SCRIPT_TABLE_PATH_MAPPING_CACHE", tableName);
        return path;
    }
}
