/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.formula.service.impl;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.formula.bean.CoreFormulaRequiredBean;
import com.sunwayworld.module.sys.formula.dao.CoreFormulaRequiredDao;
import com.sunwayworld.module.sys.formula.service.CoreFormulaRequiredService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreFormulaRequiredServiceImpl
implements CoreFormulaRequiredService {
    @Autowired
    private CoreFormulaRequiredDao formulaRequiredDao;

    @Override
    public CoreFormulaRequiredDao getDao() {
        return this.formulaRequiredDao;
    }

    @Override
    @Transactional
    public void insert(List<CoreFormulaRequiredBean> itemList) {
        itemList.forEach(i -> i.setId(ApplicationContextHelper.getNextIdentity()));
        this.getDao().insert(itemList);
    }
}
