package com.sunwayworld.emc.module.mdm.equipt.equipt.service;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptDCUConfBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcEquiptDCUConfService extends GenericService<EmcEquiptDCUConfBean, Long>{

    void insertWithEquipt(Long equiptId);

}
