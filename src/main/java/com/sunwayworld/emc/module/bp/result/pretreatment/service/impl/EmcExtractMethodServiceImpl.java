package com.sunwayworld.emc.module.bp.result.pretreatment.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.result.pretreatment.bean.EmcExtractMethodBean;
import com.sunwayworld.emc.module.bp.result.pretreatment.dao.EmcExtractMethodDao;
import com.sunwayworld.emc.module.bp.result.pretreatment.service.EmcExtractMethodService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2023年08月14日
 */
@Repository
@GikamBean
public class EmcExtractMethodServiceImpl implements EmcExtractMethodService {

    @Autowired
    private EmcExtractMethodDao extractMethodDao;

    @SuppressWarnings("unchecked")
    @Override
    public EmcExtractMethodDao getDao() {
        return extractMethodDao;
    }

    @Override
    public Long insert(RestJsonWrapperBean wrapper) {
        EmcExtractMethodBean extractMethod = wrapper.parseUnique(EmcExtractMethodBean.class);
        extractMethod.setId(ApplicationContextHelper.getNextIdentity());
        extractMethod.setOrgId(LocalContextHelper.getLoginUser().getExt$Item("parentOrgId"));
        getDao().insert(extractMethod);
        return extractMethod.getId();
    }
}
