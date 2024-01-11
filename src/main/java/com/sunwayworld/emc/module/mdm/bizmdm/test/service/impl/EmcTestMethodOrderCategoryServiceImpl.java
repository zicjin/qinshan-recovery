package com.sunwayworld.emc.module.mdm.bizmdm.test.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodOrderCategoryBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.dao.EmcTestMethodOrderCategoryDao;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodOrderCategoryService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.BeanUtils;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-09-19
 */

@Repository
public class EmcTestMethodOrderCategoryServiceImpl implements EmcTestMethodOrderCategoryService {
    @Autowired
    private EmcTestMethodOrderCategoryDao emcTestMethodOrderCategoryDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcTestMethodOrderCategoryDao getDao() {
        return emcTestMethodOrderCategoryDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(Long testMethodId, RestJsonWrapperBean jsonWrapper) {

        List<EmcTestMethodOrderCategoryBean> testMethodOrderCategoryList = jsonWrapper.parse(EmcTestMethodOrderCategoryBean.class);

        List<EmcTestMethodOrderCategoryBean> insertTestMethodOrderCategoryList = new ArrayList<EmcTestMethodOrderCategoryBean>();
        testMethodOrderCategoryList.forEach(e -> {
            EmcTestMethodOrderCategoryBean insertTestMethodOrderCategory = new EmcTestMethodOrderCategoryBean();
            BeanUtils.copyProperties(e, insertTestMethodOrderCategory);
            insertTestMethodOrderCategory.setId(ApplicationContextHelper.getNextIdentity());
            insertTestMethodOrderCategory.setTestMethodId(testMethodId);
            insertTestMethodOrderCategoryList.add(insertTestMethodOrderCategory);
        });

        getDao().insert(insertTestMethodOrderCategoryList);
        return insertTestMethodOrderCategoryList.get(0).getId();
    }

    @Override
    public Page<EmcTestMethodOrderCategoryBean> selectMaintenancePage(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return selectPagination(() -> emcTestMethodOrderCategoryDao.selectMaintenancePage(parameter), rowBounds);
    }

}
