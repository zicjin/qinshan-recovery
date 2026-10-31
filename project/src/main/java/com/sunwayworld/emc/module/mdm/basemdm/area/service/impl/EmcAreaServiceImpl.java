package com.sunwayworld.emc.module.mdm.basemdm.area.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.area.bean.EmcAreaBean;
import com.sunwayworld.emc.module.mdm.basemdm.area.bean.EmcAreaTreeDTO;
import com.sunwayworld.emc.module.mdm.basemdm.area.dao.EmcAreaDao;
import com.sunwayworld.emc.module.mdm.basemdm.area.service.EmcAreaService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.support.tree.TreeDescriptor;
import com.sunwayworld.framework.support.tree.TreeHelper;

/**
 * @author yuanhh@sunwayworld.com 2019年9月16日
 */

@Repository
@GikamBean
public class EmcAreaServiceImpl implements EmcAreaService {
    @Autowired
    private EmcAreaDao emcAreaDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcAreaDao getDao() {
        return emcAreaDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcAreaBean emcArea = jsonWrapper.parseUnique(EmcAreaBean.class);
        emcArea.setId(ApplicationContextHelper.getNextSequenceNumber(getDao().getTable()));
        getDao().insert(emcArea);
        return emcArea.getId();
    }

    public List<EmcAreaTreeDTO> selectTree(MapperParameter parameter) {
        List<EmcAreaBean> methodCategoryList = getDao().selectTreeNodeList(parameter).stream().map(e -> PersistableHelper.mapToPersistable(e, EmcAreaBean.class)).collect(Collectors.toList());
        TreeDescriptor<EmcAreaBean> descriptor = new TreeDescriptor<>("id", "parentId", "areaName", "orderNo");
        return TreeHelper.parseTreeNode(methodCategoryList, descriptor, EmcAreaTreeDTO.class);
    }
}
