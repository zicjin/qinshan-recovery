package com.sunwayworld.emc.module.mdm.bizmdm.method.service.impl;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.bizmdm.method.bean.EmcMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.method.bean.EmcMethodVersionBean;
import com.sunwayworld.emc.module.mdm.bizmdm.method.service.EmcMethodVersionService;
import com.sunwayworld.emc.module.mdm.bizmdm.method.service.PfMethodService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class PfMethodServiceImpl extends EmcMethodServiceImpl implements PfMethodService {

    @Autowired
    private EmcMethodVersionService methodVersionService;
    
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void updateVersionInfo(Long id) {
        EmcMethodBean method = getDao().selectById(id);
        List<EmcMethodVersionBean> versionList = methodVersionService.getDao().selectListByOneColumnValue(id, "METHODID");
        if (!versionList.isEmpty()) {
            EmcMethodVersionBean maxVersion = versionList.stream().max(Comparator.comparingLong(EmcMethodVersionBean::getVersionNo)).get();
            method.setMaxVersionId(maxVersion.getId());
            method.setMaxVersionNo(maxVersion.getVersionNo());
            method.setTestMethodBasis(maxVersion.getTestMethodBasis());
            method.setMaxElnTmplId(maxVersion.getElnTmplId());
            method.setExt$Item("testmethodaliasbasis",maxVersion.getExt$Item("testmethodaliasbasis"));
            getDao().update(method, "MAXVERSIONID", "MAXVERSIONNO", "TESTMETHODBASIS", "MAXELNTMPLID","TESTMETHODALIASBASIS");
        }
    }

}