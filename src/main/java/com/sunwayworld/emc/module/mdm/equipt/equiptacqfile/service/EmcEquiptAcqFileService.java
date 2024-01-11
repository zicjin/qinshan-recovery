package com.sunwayworld.emc.module.mdm.equipt.equiptacqfile.service;

import com.sunwayworld.emc.module.mdm.equipt.equiptacqfile.bean.EmcEquiptAcqFileBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

public interface EmcEquiptAcqFileService extends GenericService<EmcEquiptAcqFileBean, Long>, GenericChoosableService<EmcEquiptAcqFileBean, Long> {

    void dcuAcquisition();
}
