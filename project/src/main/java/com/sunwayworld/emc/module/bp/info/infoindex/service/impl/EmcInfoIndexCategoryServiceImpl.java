package com.sunwayworld.emc.module.bp.info.infoindex.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.info.infoindex.bean.EmcInfoIndexCategoryBean;
import com.sunwayworld.emc.module.bp.info.infoindex.dao.EmcInfoIndexCategoryDao;
import com.sunwayworld.emc.module.bp.info.infoindex.service.EmcInfoIndexCategoryService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-21
 */

@Repository
@GikamBean
public class EmcInfoIndexCategoryServiceImpl implements EmcInfoIndexCategoryService {
    @Autowired
    private EmcInfoIndexCategoryDao infoIndexCategoryDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcInfoIndexCategoryDao getDao() {
        return infoIndexCategoryDao;
    }

    @Override
    @Log(value = "插入评价标准分类", type = LogType.INSERT)
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(Long indexId, RestJsonWrapperBean jsonWrapper) {
        EmcInfoIndexCategoryBean infoIndexCategory = jsonWrapper.parseUnique(EmcInfoIndexCategoryBean.class);
        infoIndexCategory.setId(ApplicationContextHelper.getNextIdentity());
        infoIndexCategory.setIndexId(indexId);
        getDao().insert(infoIndexCategory);
        return infoIndexCategory.getId();
    }

    @Override
    @Log(value = "复制评价标准分类", type = LogType.INSERT)
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public EmcInfoIndexCategoryBean copy(Long categoryId) {
        EmcInfoIndexCategoryBean infoIndexCategory = getDao().selectById(categoryId);
        infoIndexCategory.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(infoIndexCategory);
        return infoIndexCategory;
    }
}
