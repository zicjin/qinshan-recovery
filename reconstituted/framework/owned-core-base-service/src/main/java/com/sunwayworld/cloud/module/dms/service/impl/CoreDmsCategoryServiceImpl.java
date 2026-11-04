/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.dms.service.impl;

import com.sunwayworld.cloud.module.dms.bean.CoreDmsCategoryDTO;
import com.sunwayworld.cloud.module.dms.persistent.dao.CoreDmsCategoryDao;
import com.sunwayworld.cloud.module.dms.service.CoreDmsCategoryService;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.tree.TreeDescriptor;
import com.sunwayworld.framework.support.tree.TreeHelper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreDmsCategoryServiceImpl
implements CoreDmsCategoryService {
    @Autowired
    private CoreDmsCategoryDao dmsCategoryDao;

    @Override
    public CoreDmsCategoryDao getDao() {
        return this.dmsCategoryDao;
    }

    @Override
    public List<CoreDmsCategoryDTO> selectTree(MapperParameter parameter) {
        List dmsCategoryList = this.selectAll();
        TreeDescriptor descriptor = new TreeDescriptor("id", "parentId", "dmsCategoryName", "orderNo");
        List<CoreDmsCategoryDTO> dmsCategoryDTOList = TreeHelper.parseTreeNode(dmsCategoryList, descriptor, CoreDmsCategoryDTO.class);
        TreeHelper.updateChildQty(dmsCategoryDTOList);
        dmsCategoryDTOList.forEach(e -> e.setExpanded(true));
        return dmsCategoryDTOList;
    }
}
