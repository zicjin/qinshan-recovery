package com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.dao.EmcIndexPreviewDao;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.service.EmcIndexPreviewService;

@Repository
public class EmcIndexPreviewServiceImpl implements EmcIndexPreviewService {

    @Autowired
    private EmcIndexPreviewDao indexPreviewDao;

    @SuppressWarnings("unchecked")
    @Override
    public EmcIndexPreviewDao getDao() {
        return indexPreviewDao;
    }

}
