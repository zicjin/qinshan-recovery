package com.sunwayworld.emc.module.mdm.equipt.equiptacqresult.service.impl;


import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEquiptAcqScriptBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.service.impl.EmcEquiptAcqScriptServiceImpl;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;


@Repository
@GikamBean
public class PfEquiptAcqScriptServiceImpl extends EmcEquiptAcqScriptServiceImpl {

    /**
     * 新增
     */
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {

        EmcEquiptAcqScriptBean equiptAcqScriptBean = new EmcEquiptAcqScriptBean();
        equiptAcqScriptBean.setId(ApplicationContextHelper.getNextIdentity());
        //赋值所属单位信息
        CoreUserBean user = new CoreUserBean();
        user = LocalContextHelper.getLoginUser();
        String parentOrgId = user.getExt$Item("parentorgid");
        equiptAcqScriptBean.setExt$Item("orgid", parentOrgId);
        getDao().insert(equiptAcqScriptBean);
        return equiptAcqScriptBean.getId();
    }
}