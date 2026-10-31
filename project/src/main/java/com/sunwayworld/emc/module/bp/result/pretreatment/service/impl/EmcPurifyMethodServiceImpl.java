package com.sunwayworld.emc.module.bp.result.pretreatment.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.result.pretreatment.bean.EmcPurifyMethodBean;
import com.sunwayworld.emc.module.bp.result.pretreatment.dao.EmcPurifyMethodDao;
import com.sunwayworld.emc.module.bp.result.pretreatment.service.EmcPurifyMethodService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2023年08月14日
 */
@Repository
@GikamBean
public class EmcPurifyMethodServiceImpl implements EmcPurifyMethodService {

    @Autowired
    private EmcPurifyMethodDao purifyMethodDao;

    @SuppressWarnings("unchecked")
    @Override
    public EmcPurifyMethodDao getDao() {
        return purifyMethodDao;
    }

    @Override
    public Long insert(RestJsonWrapperBean wrapper) {
        EmcPurifyMethodBean purifyMethod = wrapper.parseUnique(EmcPurifyMethodBean.class);
        purifyMethod.setId(ApplicationContextHelper.getNextIdentity());
        purifyMethod.setOrgId(LocalContextHelper.getLoginUser().getExt$Item("parentOrgId"));
        getDao().insert(purifyMethod);
        return purifyMethod.getId();
    }
}
