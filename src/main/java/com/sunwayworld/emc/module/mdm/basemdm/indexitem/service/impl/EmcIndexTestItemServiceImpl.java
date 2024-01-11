package com.sunwayworld.emc.module.mdm.basemdm.indexitem.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.indexitem.bean.EmcIndexTestItemBean;
import com.sunwayworld.emc.module.mdm.basemdm.indexitem.dao.EmcIndexTestItemDao;
import com.sunwayworld.emc.module.mdm.basemdm.indexitem.service.EmcIndexTestItemService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-17
 */

@Repository
public class EmcIndexTestItemServiceImpl implements EmcIndexTestItemService {
    @Autowired
    private EmcIndexTestItemDao indexTestItemDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcIndexTestItemDao getDao() {
        return indexTestItemDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(Long indexItemId, RestJsonWrapperBean wrapper) {
        List<EmcIndexTestItemBean> indexTestItemBeanList = wrapper.parse(EmcIndexTestItemBean.class);
        indexTestItemBeanList.stream().forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setIndexItemId(indexItemId);
        });
        getDao().insert(indexTestItemBeanList);
        return indexTestItemBeanList.get(0).getId();
    }

    @Override
    public Page<EmcIndexTestItemBean> selectIndexTestItemLimit(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        List<Map<String, Object>> indexTestItemLimitList = getDao().selectIndexTestItemLimit(parameter);
        return selectPagination(() -> indexTestItemLimitList, rowBounds);
    }
}
