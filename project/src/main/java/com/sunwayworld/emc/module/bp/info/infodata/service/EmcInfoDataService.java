package com.sunwayworld.emc.module.bp.info.infodata.service;

import java.util.List;

import com.sunwayworld.emc.module.bp.info.infodata.bean.EmcInfoDataBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcInfoDataService extends GenericService<EmcInfoDataBean, Long> {
    
    void insertReportInfoData(List<Long> reportIdList);

}
