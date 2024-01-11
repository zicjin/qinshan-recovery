package com.sunwayworld.emc.module.mdm.basemdm.indexitem.service.impl;

import com.sunwayworld.framework.exception.core.HttpErrorMessage;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ServletUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.indexitem.bean.EmcIndexItemBean;
import com.sunwayworld.emc.module.mdm.basemdm.indexitem.bean.EmcIndexTestItemBean;
import com.sunwayworld.emc.module.mdm.basemdm.indexitem.dao.EmcIndexItemDao;
import com.sunwayworld.emc.module.mdm.basemdm.indexitem.service.EmcIndexItemService;
import com.sunwayworld.emc.module.mdm.basemdm.indexitem.service.EmcIndexTestItemService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-17
 */
@GikamBean
@Repository
public class EmcIndexItemServiceImpl implements EmcIndexItemService {
    @Autowired
    private EmcIndexItemDao indexItemDao;

    @Autowired
    private EmcIndexTestItemService indexTestItemService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcIndexItemDao getDao() {
        return indexItemDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcIndexItemBean indexItem = jsonWrapper.parseUnique(EmcIndexItemBean.class);
        RestValidationResultBean validateResult = validateUnique(null, "indexItemName", indexItem.getIndexItemName());
        if(!validateResult.getValid()){
            ServletUtils.responseError(HttpErrorMessage.of(HttpStatus.BAD_REQUEST, validateResult.getMessage()));
            return 1L;
        };
        indexItem.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(indexItem);
        return indexItem.getId();
    }

    @Override
    public Page<EmcIndexTestItemBean> selectIndexTestItemLimit(RestJsonWrapperBean wrapper) {
        return indexTestItemService.selectIndexTestItemLimit(wrapper);
    }
}
