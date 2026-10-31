package com.sunwayworld.emc.module.mdm.basemdm.biology.service.impl;

import java.util.List;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.biology.bean.EmcBiologyBean;
import com.sunwayworld.emc.module.mdm.basemdm.biology.bean.EmcBiologyDTO;
import com.sunwayworld.emc.module.mdm.basemdm.biology.dao.EmcBiologyDao;
import com.sunwayworld.emc.module.mdm.basemdm.biology.service.EmcBiologyService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.tree.TreeDescriptor;
import com.sunwayworld.framework.support.tree.TreeHelper;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-28
 */

@Repository
@GikamBean
public class EmcBiologyServiceImpl implements EmcBiologyService {
    @Autowired
    private EmcBiologyDao emcBiologyDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcBiologyDao getDao() {
        return emcBiologyDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcBiologyBean emcBiology = jsonWrapper.parseUnique(EmcBiologyBean.class);
        emcBiology.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcBiology);
        return emcBiology.getId();
    }

    @Override   
    public List<EmcBiologyDTO> selectTree(MapperParameter parameter) {
        List<EmcBiologyBean> biologyBeanList = getDao().selectTreeNodeList(parameter);

        TreeDescriptor<EmcBiologyBean> descriptor = new TreeDescriptor<>("id", "speciesName");
        descriptor.setParseTreeNodeParentIdFunc(t -> "" + t.getParentId());

        return TreeHelper.parseTreeNode(biologyBeanList, descriptor, EmcBiologyDTO.class);
    }

}
