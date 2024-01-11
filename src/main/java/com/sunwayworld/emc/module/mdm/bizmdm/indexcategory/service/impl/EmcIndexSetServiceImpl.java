package com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.EmcPropertyDataService;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcIndexSetBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.dao.EmcIndexSetDao;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.service.EmcIndexSetService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author zhangsy@sunwayworld.com 2019年9月17日
 */

@Repository
@GikamBean
public class EmcIndexSetServiceImpl implements EmcIndexSetService {
    @Autowired
    private EmcIndexSetDao emcIndexSetDao;
    
    @Autowired
    private EmcPropertyDataService propertyDataService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcIndexSetDao getDao() {
        return emcIndexSetDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(String indexId, Long indexCategoryId, Long tmplId, RestJsonWrapperBean jsonWrapper) {
        List<EmcIndexSetBean> emcIndexSetBeanList = jsonWrapper.parse(EmcIndexSetBean.class);
        List<EmcPropertyDataBean> emcPropertyDataBeanList = new ArrayList<>();

        emcIndexSetBeanList.forEach(emcIndexSetBean -> {
            emcIndexSetBean.setId(ApplicationContextHelper.getNextIdentity());
            emcIndexSetBean.setIndexId(indexId);
            emcIndexSetBean.setIndexCategoryId(indexCategoryId);
            EmcPropertyDataBean emcPropertyDataBean = new EmcPropertyDataBean();
            emcPropertyDataBean.setId(ApplicationContextHelper.getNextIdentity());
            emcPropertyDataBean.setBizId(emcIndexSetBean.getId());
            emcPropertyDataBean.setTmplId(tmplId);
            emcPropertyDataBean.setTableName("T_EMC_INDEX_SET");
            emcPropertyDataBeanList.add(emcPropertyDataBean);
        });
        getDao().insert(emcIndexSetBeanList);
        propertyDataService.getDao().insert(emcPropertyDataBeanList);
        return emcIndexSetBeanList.get(0).getId();
    }

    @Override
    public List<EmcIndexSetBean> selectListByIndexIdAndItemId(Long indexId, Long itemId) {
        return getDao().selectListByIndexIdAndItemId(indexId, itemId);
    }
}
