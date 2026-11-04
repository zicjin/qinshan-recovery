/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.admarea.service.impl;

import com.sunwayworld.cloud.module.item.admarea.bean.CoreAdmAreaBean;
import com.sunwayworld.cloud.module.item.admarea.bean.CoreAdmAreaDTO;
import com.sunwayworld.cloud.module.item.admarea.persistent.dao.CoreAdmAreaDao;
import com.sunwayworld.cloud.module.item.admarea.service.CoreAdmAreaService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.tree.TreeDescriptor;
import com.sunwayworld.framework.support.tree.TreeHelper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreAdmAreaServiceImpl
implements CoreAdmAreaService {
    @Autowired
    private CoreAdmAreaDao areaDao;

    @Override
    public CoreAdmAreaDao getDao() {
        return this.areaDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public String insert(RestJsonWrapperBean wrapper) {
        CoreAdmAreaBean admArea = wrapper.parseUnique(CoreAdmAreaBean.class);
        if ("".equals(admArea.getParentId())) {
            admArea.setParentId(null);
        }
        this.getDao().insert(admArea);
        return admArea.getId();
    }

    @Override
    public List<CoreAdmAreaDTO> selectTree(MapperParameter parameter) {
        List<CoreAdmAreaBean> areaList = this.getDao().selectTreeNodeList(parameter);
        TreeDescriptor descriptor = new TreeDescriptor("id", "parentId", "admAreaName");
        List<CoreAdmAreaDTO> admAreaDTOList = TreeHelper.parseTreeNode(areaList, descriptor, CoreAdmAreaDTO.class);
        return admAreaDTOList;
    }

    @Override
    public List<CoreAdmAreaDTO> selectSelectableList() {
        List areaList = this.getDao().selectAll();
        TreeDescriptor descriptor = new TreeDescriptor("id", "parentId", "admAreaName");
        return TreeHelper.parseTreeNode(areaList, descriptor, CoreAdmAreaDTO.class);
    }
}
