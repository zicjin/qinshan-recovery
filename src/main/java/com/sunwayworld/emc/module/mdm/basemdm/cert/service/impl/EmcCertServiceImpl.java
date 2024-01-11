package com.sunwayworld.emc.module.mdm.basemdm.cert.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.cert.bean.EmcCertBean;
import com.sunwayworld.emc.module.mdm.basemdm.cert.bean.EmcCertLogBean;
import com.sunwayworld.emc.module.mdm.basemdm.cert.dao.EmcCertDao;
import com.sunwayworld.emc.module.mdm.basemdm.cert.service.EmcCertLogService;
import com.sunwayworld.emc.module.mdm.basemdm.cert.service.EmcCertService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-08
 */

@Repository
@GikamBean
public class EmcCertServiceImpl implements EmcCertService {
    @Autowired
    private EmcCertDao emcCertDao;

    @Autowired
    private EmcCertLogService certLogService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcCertDao getDao() {
        return emcCertDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(String id, RestJsonWrapperBean jsonWrapper) {
        EmcCertBean emcCert = jsonWrapper.parseUnique(EmcCertBean.class);
        emcCert.setEmpId(id);
        emcCert.setId(ApplicationContextHelper.getNextIdentity());
        emcCert.setStatus("new");
        emcCert.setOrderNo(ApplicationContextHelper.getNextSequenceNumber(getDao().getTable()));
        getDao().insert(emcCert);

        EmcCertLogBean emcCertLogBean = new EmcCertLogBean();
        emcCertLogBean.setId(ApplicationContextHelper.getNextIdentity());
        emcCertLogBean.setCertId(emcCert.getId());
        emcCertLogBean.setLogContent("new");
        CoreUserBean loginUser = LocalContextHelper.getLoginUser();
        emcCertLogBean.setLoginUserId(loginUser.getId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        emcCertLogBean.setLoginUserTime(sdf.format(new Date()));
        certLogService.getDao().insert(emcCertLogBean);
        return emcCert.getId();
    }

    @Override
    public Long updateCertMethodAuth(Long id, RestJsonWrapperBean wrapper) {
        EmcCertBean emcCert = wrapper.parseUnique(EmcCertBean.class);
        emcCert.setId(id);
        emcCert.setStatus("auth");
        getDao().updateIfChanged(emcCert);

        EmcCertLogBean emcCertLogBean = new EmcCertLogBean();
        emcCertLogBean.setId(ApplicationContextHelper.getNextIdentity());
        emcCertLogBean.setCertId(emcCert.getId());
        emcCertLogBean.setLogContent("auth");
        CoreUserBean loginUser = LocalContextHelper.getLoginUser();
        emcCertLogBean.setLoginUserId(loginUser.getId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        emcCertLogBean.setLoginUserTime(sdf.format(new Date()));
        certLogService.getDao().insert(emcCertLogBean);
        return emcCert.getId();
    }

    @Override
    public Page<EmcCertBean> selectOverdueCert(RestJsonWrapperBean wrapper) {

        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();

        return selectPagination(() -> getDao().selectOverdueCert(parameter), rowBounds);
    }

}
