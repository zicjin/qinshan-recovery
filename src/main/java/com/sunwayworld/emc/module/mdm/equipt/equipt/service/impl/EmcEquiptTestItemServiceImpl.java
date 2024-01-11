package com.sunwayworld.emc.module.mdm.equipt.equipt.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestItemBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.EmcEquiptTestItemDao;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptTestItemService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author magh@sunwayworld.com
 * @date 2019-11-13
 */

@Repository
public class EmcEquiptTestItemServiceImpl implements EmcEquiptTestItemService {
    @Autowired
    private EmcEquiptTestItemDao emcEquiptTestItemDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcEquiptTestItemDao getDao() {
        return emcEquiptTestItemDao;
    }

    @Override
    @Transactional
    public Long insert(Long equiptId, RestJsonWrapperBean jsonWrapper) {
        List<EmcEquiptTestItemBean> equiptTestItemList = jsonWrapper.parse(EmcEquiptTestItemBean.class);
        equiptTestItemList.stream().forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setEquiptId(equiptId);
        });
        getDao().insert(equiptTestItemList);
        return equiptTestItemList.get(0).getId();
    }

}
