package com.sunwayworld.emc.module.mdm.basemdm.mtl.service;

import org.springframework.stereotype.Repository;

import com.sunwayworld.framework.spring.annotation.GikamBean;


@Repository
@GikamBean
public interface PfMtlService extends EmcMtlService {

    void updateStockQty(String id);
    
}