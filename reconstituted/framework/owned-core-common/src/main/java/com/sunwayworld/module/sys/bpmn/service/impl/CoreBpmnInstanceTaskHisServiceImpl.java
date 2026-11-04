/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskHisBean;
import com.sunwayworld.module.sys.bpmn.dao.CoreBpmnInstanceTaskHisDao;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnInstanceTaskHisService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreBpmnInstanceTaskHisServiceImpl
implements CoreBpmnInstanceTaskHisService {
    @Autowired
    private CoreBpmnInstanceTaskHisDao instanceTaskHisDao;

    @Override
    public CoreBpmnInstanceTaskHisDao getDao() {
        return this.instanceTaskHisDao;
    }

    @Override
    public List<CoreBpmnInstanceTaskHisBean> selectLatestTaskHisList(List<CoreBpmnInstanceTaskHisBean> filterList) {
        return this.getDao().selectLatestTaskHisList(filterList);
    }
}
