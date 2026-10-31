package com.sunwayworld.emc.module.mdm.filemodification.service;

import com.sunwayworld.emc.module.mdm.filemodification.bean.EmcFileModificationBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

/**
 * @author zhanghy@sunwayworld.com 2020-09-01
 */
public interface EmcFileModificationService extends GenericService<EmcFileModificationBean, Long>, GenericAuditableService<EmcFileModificationBean, Long> ,GenericChoosableService<EmcFileModificationBean, Long> {

}
