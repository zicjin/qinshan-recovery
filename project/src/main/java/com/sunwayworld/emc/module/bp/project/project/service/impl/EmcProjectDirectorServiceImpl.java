package com.sunwayworld.emc.module.bp.project.project.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectDirectorBean;
import com.sunwayworld.emc.module.bp.project.project.dao.EmcProjectDirectorDao;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectDirectorService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcProjectDirectorServiceImpl implements EmcProjectDirectorService {
    @Autowired
    private EmcProjectDirectorDao projectDirectorDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcProjectDirectorDao getDao() {
        return projectDirectorDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        List<EmcProjectDirectorBean> projectDirectorList = jsonWrapper.parse(EmcProjectDirectorBean.class);
        projectDirectorList.stream().forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
        });
        getDao().insert(projectDirectorList);
        return projectDirectorList.get(0).getId();
    }

}
